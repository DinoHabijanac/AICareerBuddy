using AICareerBuddy_Entities.Entities;
using GroqSharp;
using GroqSharp.Models;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks;

namespace AICareerBuddy_BussinesLayer.Services
{

    public class ChatbotService
    {
        private static string ApiKey { get; set; }
        private static string ApiModel { get; set; }

        private IGroqClient GroqClient { get; set; }

        public ChatbotService(string apiKey, string apiModel)
        {
            ApiKey = apiKey;
            ApiModel = apiModel;
            GroqClient = new GroqClient(apiKey, apiModel)
            .SetTemperature(0.5)
            .SetMaxTokens(1024)
            .SetTopP(1)
            .SetStop("NONE")
            .SetStructuredRetryPolicy(5);
        }

        public async Task<string> GetChatResponse(ObservableCollection<Message> messages)
        {
            var response = await GroqClient.CreateChatCompletionAsync(messages.ToArray());
            return response;
        }

        public async Task<string> GetResumeAnalysisAsync(string docText)
        {
            if (string.IsNullOrWhiteSpace(docText)) { throw new ArgumentException("Document text is empty", nameof(docText)); }

            var apiKey = ApiKey;
            var model = string.IsNullOrWhiteSpace(ApiModel) ? "groq/compound" : ApiModel;

            using var http = new HttpClient();
            http.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", apiKey);
            http.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));

            var userPrompt = "Analiziraj priloženi životopis i ocijenite ga od 1 do 10. Dajte ocjenu i sažet povratni komentar u najviše 100 riječi. Neka povratne informacije obavezno budu na hrvatskom jeziku\n" + docText;

            var payload = new
            {
                model = model,
                messages = new[] { new { role = "user", content = userPrompt } }
            };

            var options = new JsonSerializerOptions { PropertyNamingPolicy = JsonNamingPolicy.CamelCase };
            var json = JsonSerializer.Serialize(payload, options);
            using var content = new StringContent(json, Encoding.UTF8, "application/json");
            using var resp = await http.PostAsync("https://api.groq.com/openai/v1/chat/completions", content);
            var respJson = await resp.Content.ReadAsStringAsync();
            if (!resp.IsSuccessStatusCode) { throw new InvalidOperationException($"Groq API {(int)resp.StatusCode}: {respJson}"); }

            string feedback = respJson;
            try
            {
                using var doc = JsonDocument.Parse(respJson);
                if (doc.RootElement.TryGetProperty("choices", out var choices) && choices.GetArrayLength() >0)
                {
                    var msg = choices[0].GetProperty("message");
                    if (msg.TryGetProperty("content", out var cont)) { feedback = cont.GetString() ?? respJson; }
                }
            }
            catch { }

            return feedback;
        }

        public async Task<ResumeImprovementSuggestions> GetImprovementSuggestionsAsync(string docText, string previousAnalysis)
        {
            if (string.IsNullOrWhiteSpace(docText)) { throw new ArgumentException("Document text is empty", nameof(docText)); }
            if (string.IsNullOrWhiteSpace(previousAnalysis)) { throw new ArgumentException("Previous analysis is empty", nameof(previousAnalysis)); }

            var apiKey = ApiKey;
            var model = string.IsNullOrWhiteSpace(ApiModel) ? "groq/compound" : ApiModel;

            using var http = new HttpClient();
            http.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", apiKey);
            http.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));

            var userPrompt =
                "Prethodni analiza ovog životopisa glasi:\n" +
                "\"" + previousAnalysis + "\"\n\n" +
                "Originalni životopis:\n" +
                docText + "\n\n" +
                "Na temelju prethodne analize i originalnog životopisa, daj SAMO strukturirane prijedloge poboljšanja " +
                "grupirane po sekcijama. Obavezne sekcije su: \"Radno iskustvo\", \"Vještine\", \"Obrazovanje\", \"Kontaktni podaci\", \"Struktura i formatiranje\". " +
                "Ako postoji nešto još pametno dodaj i tu sekciju.\n\n" +
                "Za svaku sekciju napišeš:\n" +
                "  - title: naziv sekcije\n" +
                "  - status: \"U redu\" ili \"Postoji prostor za poboljšanje\"\n" +
                "  - description: 1-2 rečenice što je dobro ili što konkretno treba popraviti/dodati\n\n" +
                "Na kraju dodaj i polje \"overallSummary\" – jedna rečenica ukupni zaključak.\n\n" +
                "IMPORTANT: Odgovor mora biti ISKLJUČIVO valjan JSON u ovom formatu, bez ikakvog teksta van JSON-a:\n" +
                "{\n" +
                "  \"sections\": [\n" +
                "    { \"title\": \"...\", \"status\": \"...\", \"description\": \"...\" },\n" +
                "    ...\n" +
                "  ],\n" +
                "  \"overallSummary\": \"...\"\n" +
                "}\n";

            var payload = new
            {
                model = model,
                messages = new[] { new { role = "user", content = userPrompt } }
            };

            var options = new JsonSerializerOptions { PropertyNamingPolicy = JsonNamingPolicy.CamelCase };
            var json = JsonSerializer.Serialize(payload, options);
            using var content = new StringContent(json, Encoding.UTF8, "application/json");
            using var resp = await http.PostAsync("https://api.groq.com/openai/v1/chat/completions", content);
            var respJson = await resp.Content.ReadAsStringAsync();

            if (!resp.IsSuccessStatusCode) { throw new InvalidOperationException($"Groq API {(int)resp.StatusCode}: {respJson}"); }

            string rawContent = respJson;
            try
            {
                using var doc = JsonDocument.Parse(respJson);
                if (doc.RootElement.TryGetProperty("choices", out var choices) && choices.GetArrayLength() > 0)
                {
                    var msg = choices[0].GetProperty("message");
                    if (msg.TryGetProperty("content", out var cont)) { rawContent = cont.GetString() ?? respJson; }
                }
            }
            catch { }

            // Strip markdown code-fence if Groq wraps it
            rawContent = rawContent.Trim();
            if (rawContent.StartsWith("```json", StringComparison.OrdinalIgnoreCase)) { rawContent = rawContent.Substring(7); }
            else if (rawContent.StartsWith("```", StringComparison.OrdinalIgnoreCase)) { rawContent = rawContent.Substring(3); }
            if (rawContent.EndsWith("```", StringComparison.OrdinalIgnoreCase)) { rawContent = rawContent.Substring(0, rawContent.Length - 3); }
            rawContent = rawContent.Trim();

            // Parse into DTO
            try
            {
                var parseOptions = new JsonSerializerOptions { PropertyNameCaseInsensitive = true };
                var result = JsonSerializer.Deserialize<ResumeImprovementSuggestions>(rawContent, parseOptions);
                if (result != null && result.Sections.Count > 0)
                    return result;
            }
            catch { }

            // Fallback: wrap raw text as single section
            return new ResumeImprovementSuggestions
            {
                Sections = new List<ImprovementSection>
                {
                    new ImprovementSection
                    {
                        Title = "Opće napomene",
                        Status = "Postoji prostor za poboljšanje",
                        Description = rawContent
                    }
                },
                OverallSummary = "AI nije uspio strukturirati odgovor – pogledaj opće napomene gore."
            };
        }
    }
}
