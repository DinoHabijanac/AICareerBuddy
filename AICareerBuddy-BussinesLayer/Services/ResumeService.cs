using AICareerBuddy_BussinesLayer.Interfaces;
using AICareerBuddy_DataAccessLayer.Repositories;
using AICareerBuddy_Entities.Entities;
using Azure;
using Azure.Storage.Files.Shares;
using Microsoft.AspNetCore.Http;
using Microsoft.EntityFrameworkCore;
using System.IO;
using System.Text;
using UglyToad.PdfPig;
using DocumentFormat.OpenXml.Packaging;
using AICareerBuddy_BussinesLayer.Services;

namespace AICareerBuddy_BussinesLogic.Services
{
    public class ResumeService : IResumeService
    {
        private ResumeFileRepository Repository;

        public ResumeService()
        {
            Repository = new ResumeFileRepository();
        }

        public async Task<List<ResumeFileInfo>> GetResumes()
        {
            return await Repository.GetAll().ToListAsync();
        }

        public async Task<ResumeFileInfo> GetResume(int userId)
        {
            var resume = await Repository.GetResume(userId).FirstOrDefaultAsync();
            if (resume == null) { throw new FileNotFoundException("neima"); }
            return resume;
        }

        private static string connectionString = "DefaultEndpointsProtocol=https;AccountName=portalfiles1;AccountKey=yKjraClCZvUMPj2MVMlTldfZVT2by1VBEiMCcdAQ3qUcwwRokjDHNkuy0SPVilikO6zIaLKylTjn+AStoAO6+g==;EndpointSuffix=core.windows.net";
        private static string shareName = "infoguardians";

        public async Task<ResumeFileInfo> PostResume(IFormFile file, int userId)
        {
            var allowedExtensions = new List<string>
            {
                ".pdf",
                ".doc",
                ".docx"
            };

            if (file == null || file.Length == 0)
            {
                throw new ArgumentNullException("File not provided");
            }
            else if (allowedExtensions.Contains(Path.GetExtension(file.FileName)) == false)
            {
                throw new FormatException("Wrong file extension - allowed (.pdf, .doc, .docx)");
            }
            else if (file.Length > 5 * 1024 * 1024)
            {
                throw new ArgumentOutOfRangeException("File is to large (>5MB)");
            }
            else if (userId <= 0)
            {
                throw new ArgumentOutOfRangeException($"User ID is has a negative value - {userId}");
            }
            else
            {
                try
                {
                    var existingResume = await Repository.GetResume(userId)
                        .OrderByDescending(r => r.Id)
                        .FirstOrDefaultAsync();

                    if (existingResume != null)
                    {
                        Console.WriteLine($"PostResume: Deleting existing resume {existingResume.Name}");

                        var shareClientOld = new ShareClient(connectionString, shareName);
                        var rootDirOld = shareClientOld.GetRootDirectoryClient();
                        var oldFileClient = rootDirOld.GetFileClient(existingResume.Name);
                        await oldFileClient.DeleteIfExistsAsync();
                        await Repository.Remove(existingResume);
                    }

                    var shareClient = new ShareClient(connectionString, shareName);
                    await shareClient.CreateIfNotExistsAsync();

                    var rootDir = shareClient.GetRootDirectoryClient();

                    var fileName = Guid.NewGuid().ToString() + Path.GetExtension(file.FileName);
                    Console.WriteLine($"PostResume: Creating new file {fileName}");

                    var fileClient = rootDir.GetFileClient(fileName);

                    await fileClient.CreateAsync(file.Length);

                    using (var stream = file.OpenReadStream())
                    {
                        if (stream.CanSeek)
                        { stream.Position = 0; }

                        await fileClient.UploadRangeAsync(new HttpRange(0, file.Length), stream);
                    }

                    var fileInfo = new ResumeFileInfo()
                    {
                        Name = fileName,
                        Path = fileClient.Uri.ToString(),
                        Extension = fileName.Substring(fileName.LastIndexOf('.')),
                        CreateDate = DateOnly.FromDateTime(DateTime.Now),
                        UserId = userId
                    };

                    var result = await Repository.Add(fileInfo);

                    if (result == 1)
                    {
                        Console.WriteLine($"PostResume: Successfully saved {fileName}");
                        return fileInfo;
                    }
                    else
                    {
                        Console.WriteLine("PostResume: Failed to save to database");
                        return null;
                    }
                }
                catch (Exception ex)
                {
                    Console.WriteLine($"PostResume ERROR: {ex.Message}");
                    throw new InvalidOperationException(ex.Message);
                }
            }
        }

        public async Task<bool> DeleteResume(int userId)
        {
            try
            {
                // Get the MOST RECENT resume for this user
                var resume = await Repository.GetResume(userId)
                    .OrderByDescending(r => r.Id)
                    .FirstOrDefaultAsync();

                if (resume == null)
                {
                    Console.WriteLine($"No resume found for userId: {userId}");
                    return false;
                }

                Console.WriteLine($"Delete attempt for file: {resume.Name}");

                // Delete file from Azure File Share
                var shareClient = new ShareClient(connectionString, shareName);
                var rootDir = shareClient.GetRootDirectoryClient();
                var fileClient = rootDir.GetFileClient(resume.Name);

                var deleteResponse = await fileClient.DeleteIfExistsAsync();

                Console.WriteLine($"File deleted from Azure: {deleteResponse.Value}");
                Console.WriteLine($"Share name: {shareName}");

                // Delete record from database
                var result = await Repository.Remove(resume);

                Console.WriteLine($"Database record deleted: {result == 1}");

                return result == 1;
            }
            catch (Exception ex)
            {
                Console.WriteLine($"DELETE ERROR: {ex.Message}");
                Console.WriteLine($"Stack trace: {ex.StackTrace}");
                throw new InvalidOperationException($"Error deleting resume: {ex.Message}");
            }
        }

        public async Task<ResumeFileInfo> UpdateResume(IFormFile file, int userId)
        {
            try
            {
                Console.WriteLine($"=== UPDATE RESUME START for userId: {userId} ===");

                // PostResume now handles deleting old files automatically
                var newResume = await PostResume(file, userId);

                Console.WriteLine($"=== UPDATE RESUME COMPLETE: {newResume?.Name} ===");

                return newResume;
            }
            catch (Exception ex)
            {
                Console.WriteLine($"UPDATE ERROR: {ex.Message}");
                Console.WriteLine($"Stack trace: {ex.StackTrace}");
                throw new InvalidOperationException($"Error updating resume: {ex.Message}");
            }
        }

        private static string ExtractTextFromPdf(Stream pdfStream)
        {
            pdfStream.Position = 0;
            var sb = new StringBuilder();
            using (var doc = PdfDocument.Open(pdfStream))
            {
                foreach (var page in doc.GetPages())
                {
                    var text = page.Text;
                    if (!string.IsNullOrWhiteSpace(text)) sb.AppendLine(text);
                }
            }
            return sb.ToString();
        }

        private static string ExtractTextFromDocx(Stream docxStream)
        {
            docxStream.Position = 0;
            using var ms = new MemoryStream();
            docxStream.CopyTo(ms);
            ms.Position = 0;
            using var word = WordprocessingDocument.Open(ms, false);
            var body = word.MainDocumentPart?.Document?.Body;
            return body?.InnerText ?? string.Empty;
        }

        public async Task<ResumeAIFeedback> GetResumeAnalysisAI(int id)
        {
            var resume = await GetResume(id);
            if (resume == null)
            {
                throw new FileNotFoundException($"No resume found for user id {id}");
            }

            var shareClient = new ShareClient(connectionString, shareName);
            var rootDir = shareClient.GetRootDirectoryClient();
            var fileClient = rootDir.GetFileClient(resume.Name);

            var downloadResponse = await fileClient.DownloadAsync();
            using var contentStream = downloadResponse.Value.Content;
            var memoryStream = new MemoryStream();
            await contentStream.CopyToAsync(memoryStream);
            memoryStream.Position = 0;

            string docText = string.Empty;
            var ext = (resume.Extension ?? string.Empty).ToLowerInvariant();
            try
            {
                if (ext == ".pdf") { docText = ExtractTextFromPdf(memoryStream); }
                else if (ext == ".docx") { docText = ExtractTextFromDocx(memoryStream); }
                else { docText = Encoding.UTF8.GetString(memoryStream.ToArray()); }
            }
            catch (Exception ex)
            {
                throw new InvalidOperationException($"Error extracting text: {ex.Message}");
            }

            if (string.IsNullOrWhiteSpace(docText)) throw new InvalidOperationException("No extractable text found in resume");

            var chatbot = new ChatbotService("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", "groq/compound");
            var feedback = await chatbot.GetResumeAnalysisAsync(docText);

            return new ResumeAIFeedback { Feedback = feedback };
        }

        public async Task<ResumeImprovementSuggestions> GetResumeImprovementsAI(int userId)
        {
            var resume = await GetResume(userId);
            if (resume == null) { throw new FileNotFoundException($"No resume found for user id {userId}"); }

            var shareClient = new ShareClient(connectionString, shareName);
            var rootDir = shareClient.GetRootDirectoryClient();
            var fileClient = rootDir.GetFileClient(resume.Name);

            var downloadResponse = await fileClient.DownloadAsync();
            using var contentStream = downloadResponse.Value.Content;
            var memoryStream = new MemoryStream();
            await contentStream.CopyToAsync(memoryStream);
            memoryStream.Position = 0;

            string docText = string.Empty;
            var ext = (resume.Extension ?? string.Empty).ToLowerInvariant();
            try
            {
                if (ext == ".pdf") { docText = ExtractTextFromPdf(memoryStream); }
                else if (ext == ".docx") { docText = ExtractTextFromDocx(memoryStream); }
                else { docText = Encoding.UTF8.GetString(memoryStream.ToArray()); }
            }
            catch (Exception ex)
            {
                throw new InvalidOperationException($"Error extracting text: {ex.Message}");
            }

            if (string.IsNullOrWhiteSpace(docText)) { throw new InvalidOperationException("No extractable text found in resume"); }

            var chatbot = new ChatbotService("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", "groq/compound");

            string previousAnalysis;
            try
            {
                previousAnalysis = await chatbot.GetResumeAnalysisAsync(docText);
            }
            catch (Exception ex)
            {
                throw new InvalidOperationException($"Error during initial analysis for improvements: {ex.Message}");
            }

            var suggestions = await chatbot.GetImprovementSuggestionsAsync(docText, previousAnalysis);
            return suggestions;
        }
    }
}