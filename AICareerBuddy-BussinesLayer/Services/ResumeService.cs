using AICareerBuddy_DataAccessLayer.Repositories;
using AICareerBuddy_Entities.Entities;
using Azure;
using Azure.Storage;
using Azure.Storage.Blobs;
using Azure.Storage.Files.Shares;
using Azure.Storage.Files.Shares.Models;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.CodeAnalysis.CSharp;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System.Threading.Tasks;
using static System.Net.Mime.MediaTypeNames;

namespace AICareerBuddy_DataAccessLayer.Services
{
    public class ResumeService
    {
        public static List<Resume> GetResumes()
        {
            //implementiraj

            //return ResumeRepo.GetResumes().ToList();
            return new List<Resume> { new Resume { Id = 1, Name = "Franja" }, new Resume { Id = 2, Name = "Anta" } };
        }

        //PROMJENITI NA studentski račun
        private static string connectionString = "DefaultEndpointsProtocol=https;AccountName=portalfiles1;AccountKey=yKjraClCZvUMPj2MVMlTldfZVT2by1VBEiMCcdAQ3qUcwwRokjDHNkuy0SPVilikO6zIaLKylTjn+AStoAO6+g==;EndpointSuffix=core.windows.net";
        private static string shareName = "portalfiles";

        public async static Task<IActionResult> PostResume(IFormFile file)
        {
            if (file == null || file.Length == 0)
                return new BadRequestObjectResult("No file provided.");

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
                return new OkObjectResult(new { FileName = fileName, Uri = fileClient.Uri.ToString() });
            }
            catch (Exception ex)
            {
                return new ObjectResult(ex.Message) { StatusCode = 500 };
            }
            ///IMPLEMENTIRAJ SPREMANJE GUID-a U BAZU (PREKO REPO-A) TAKO DA SE MOŽE DOHVATITI FILE    
        }
    }
}