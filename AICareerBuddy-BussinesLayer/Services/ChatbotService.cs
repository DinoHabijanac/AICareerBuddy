using GroqSharp;
using GroqSharp.Models;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
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
            .SetStructuredRetryPolicy(5)
            .SetDefaultSystemMessage(new Message {Content = "" });
        }

        public async Task<string> GetChatResponse(ObservableCollection<Message> messages)
        {
            var response = await GroqClient.CreateChatCompletionAsync(messages.ToArray());
            return response;
        }
    }
}
