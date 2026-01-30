using GroqSharp;
using GroqSharp.Models;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Text.Json;

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
    }
}
