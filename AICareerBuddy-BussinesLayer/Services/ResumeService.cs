using AICareerBuddy_BussinesLayer.Interfaces;
using AICareerBuddy_DataAccessLayer.Repositories;
using AICareerBuddy_Entities.Entities;
using Azure;
using Azure.Storage.Files.Shares;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;


namespace AICareerBuddy_BussinesLogic.Services
{
    public class ResumeService : IResumeService
    {
        public List<Resume> GetResumes()
        {
            //implementiraj

            //return ResumeRepo.GetResumes().ToList();
            return new List<Resume> { new Resume { Id = 1, Name = "Franja" }, new Resume { Id = 2, Name = "Anta" } };
        }

        public Resume GetResume(int id)
        {
            //implementiraj

            //return ResumeRepo.GetResume();
            return new Resume();
        }

        //PROMJENITI NA studentski račun
        private static string connectionString = "DefaultEndpointsProtocol=https;AccountName=portalfiles1;AccountKey=yKjraClCZvUMPj2MVMlTldfZVT2by1VBEiMCcdAQ3qUcwwRokjDHNkuy0SPVilikO6zIaLKylTjn+AStoAO6+g==;EndpointSuffix=core.windows.net";
        private static string shareName = "portalfiles";

        public async Task<FilesInfo> PostResume(IFormFile file)
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
                    return new FilesInfo()
                    {
                        ///IMPLEMENTIRAJ SPREMANJE FILE INFO-za svaki resume uploadan u bazu isto
                        Name = fileName,
                        Path = fileClient.Uri.ToString(),
                        Extension = fileName.Substring(fileName.LastIndexOf('.'))
                    };
                }
                catch (Exception ex)
                {
                    throw new InvalidOperationException(ex.Message);
                }
            }

            ///IMPLEMENTIRAJ SPREMANJE GUID-a U BAZU (PREKO REPO-A) TAKO DA SE MOŽE DOHVATITI FILE    
        }
    }
}