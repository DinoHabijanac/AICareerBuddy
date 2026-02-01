package com.example.core.models
data class ImprovementSection(
    val title: String,
    val status: String,
    val description: String
)

data class ResumeImprovements(
    val sections: List<ImprovementSection>,
    val overallSummary: String
)