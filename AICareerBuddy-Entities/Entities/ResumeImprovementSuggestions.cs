using System;
using System.Collections.Generic;
using System.Text.Json.Serialization;

namespace AICareerBuddy_Entities.Entities
{
    public class ImprovementSection
    {
        [JsonPropertyName("title")]
        public string Title { get; set; } = string.Empty;

        [JsonPropertyName("status")]
        public string Status { get; set; } = string.Empty;

        [JsonPropertyName("description")]
        public string Description { get; set; } = string.Empty;
    }

    public class ResumeImprovementSuggestions
    {
        [JsonPropertyName("sections")]
        public List<ImprovementSection> Sections { get; set; } = new();

        [JsonPropertyName("overallSummary")]
        public string OverallSummary { get; set; } = string.Empty;
    }
}