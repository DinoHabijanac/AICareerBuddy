using AICareerBuddy_BussinesLayer.Interfaces;
using AICareerBuddy_DataAccessLayer.Repositories;
using AICareerBuddy_Entities.Entities;
using Azure;
using Azure.Storage.Files.Shares;
using Microsoft.AspNetCore.Http;
using Microsoft.EntityFrameworkCore;

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
            return await Repository.GetResume(userId).FirstOrDefaultAsync();
        }

        //PROMJENITI NA studentski račun
        private static string connectionString = "DefaultEndpointsProtocol=https;AccountName=portalfiles1;AccountKey=yKjraClCZvUMPj2MVMlTldfZVT2by1VBEiMCcdAQ3qUcwwRokjDHNkuy0SPVilikO6zIaLKylTjn+AStoAO6+g==;EndpointSuffix=core.windows.net";
        private static string shareName = "portalfiles";

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
                throw new ArgumentOutOfRangeException("User ID is has a negative value");
            }
            else
            {
                try
                {
                    var shareClient = new ShareClient(connectionString, shareName);
                    await shareClient.CreateIfNotExistsAsync();

                    var rootDir = shareClient.GetRootDirectoryClient();

                    var fileName = Guid.NewGuid().ToString() + Path.GetExtension(file.FileName);
                    var fileClient = rootDir.GetFileClient(fileName);

                    await fileClient.CreateAsync(file.Length);
                    using (var stream = file.OpenReadStream())
                    {
                        if (stream.CanSeek)
                            stream.Position = 0;

                        await fileClient.UploadRangeAsync(new HttpRange(0, file.Length), stream);
                    }

                    var fileInfo = new ResumeFileInfo()
                    {
                        Name = fileName,
                        Path = fileClient.Uri.ToString(),
                        Extension = fileName.Substring(fileName.LastIndexOf('.')),
                        UserId = userId
                    };
                    var result = await Repository.Add(fileInfo);
                    if (result == 1) return fileInfo;
                    else return null;
                }
                catch (Exception ex)
                {
                    throw new InvalidOperationException(ex.Message);
                }
            }
        }
    }
}