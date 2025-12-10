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

        //PROMJENITI NA studentski račun
        private static string connectionString = "DefaultEndpointsProtocol=https;AccountName=infoguardians;AccountKey=nvu6Lea2QGu1IoVTaBKLgTWyTZM68vFsKp+bR5FItKOtJmJeurRJgWi1+J41OVxJIzs66nMvIBdS+AStEUv6MA==;EndpointSuffix=core.windows.net";
        private static string shareName = "infoguardians";

        public ResumeService()
        {
            Repository = new ResumeFileRepository();
        }

        public async Task<List<ResumeFileInfo>> GetResumes()
        {
            return await Repository.GetAll().ToListAsync();
        }

        public async Task<ResumeFileInfo> GetResume(int id)
        {
            var resume = await Repository.GetAll().Where(r => r.Id == id).FirstOrDefaultAsync();
            return resume;
        }

        public async Task<ResumeFileInfo> GetResumeByUserId(int userId)
        {
            var resume = await Repository.GetResume(userId).FirstOrDefaultAsync();
            return resume;
        }

        public async Task<ResumeFileInfo> PostResume(IFormFile file, int userId)
        {
            ValidateFile(file, userId);

            try
            {
                // No duplicate check here - it's done in the controller
                var fileInfo = await UploadFileToAzure(file, userId);
                var result = await Repository.Add(fileInfo);

                if (result == 1) return fileInfo;
                else return null;
            }
            catch (Exception ex)
            {
                throw new InvalidOperationException(ex.Message);
            }
        }

        public async Task<ResumeFileInfo> UpdateResume(int id, IFormFile file, int userId)
        {
            ValidateFile(file, userId);

            try
            {
                // Get existing resume
                var existingResume = await Repository.GetAll()
                    .Where(r => r.Id == id && r.UserId == userId)
                    .FirstOrDefaultAsync();

                if (existingResume == null)
                {
                    throw new KeyNotFoundException($"Resume with ID {id} not found for user {userId}");
                }

                // Delete old file from Azure
                await DeleteFileFromAzure(existingResume.Name);

                // Upload new file
                var newFileInfo = await UploadFileToAzure(file, userId);

                // Update database record
                existingResume.Name = newFileInfo.Name;
                existingResume.Path = newFileInfo.Path;
                existingResume.Extension = newFileInfo.Extension;
                existingResume.Size = newFileInfo.Size;
                existingResume.CreateDate = DateOnly.FromDateTime(DateTime.Now);

                var result = await Repository.Update(existingResume);

                if (result == 1) return existingResume;
                else throw new InvalidOperationException("Failed to update resume in database");
            }
            catch (Exception ex)
            {
                throw new InvalidOperationException($"Error updating resume: {ex.Message}");
            }
        }

        public async Task<bool> DeleteResume(int id, int userId)
        {
            try
            {
                // Get existing resume
                var existingResume = await Repository.GetAll()
                    .Where(r => r.Id == id && r.UserId == userId)
                    .FirstOrDefaultAsync();

                if (existingResume == null)
                {
                    throw new KeyNotFoundException($"Resume with ID {id} not found for user {userId}");
                }

                // Delete file from Azure
                await DeleteFileFromAzure(existingResume.Name);

                // Delete from database
                var result = await Repository.Remove(existingResume);

                return result == 1;
            }
            catch (Exception ex)
            {
                throw new InvalidOperationException($"Error deleting resume: {ex.Message}");
            }
        }

        // Helper methods
        private void ValidateFile(IFormFile file, int userId)
        {
            var allowedExtensions = new List<string> { ".pdf", ".doc", ".docx" };

            if (file == null || file.Length == 0)
            {
                throw new ArgumentNullException("File not provided");
            }

            if (!allowedExtensions.Contains(Path.GetExtension(file.FileName).ToLower()))
            {
                throw new FormatException("Wrong file extension - allowed (.pdf, .doc, .docx)");
            }

            if (file.Length > 5 * 1024 * 1024)
            {
                throw new ArgumentOutOfRangeException("File is too large (>5MB)");
            }

            if (userId <= 0)
            {
                throw new ArgumentOutOfRangeException($"User ID has a negative value - {userId}");
            }
        }

        private async Task<ResumeFileInfo> UploadFileToAzure(IFormFile file, int userId)
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

            return new ResumeFileInfo()
            {
                Name = fileName,
                Path = fileClient.Uri.ToString(),
                Extension = Path.GetExtension(fileName),
                Size = file.Length,
                CreateDate = DateOnly.FromDateTime(DateTime.Now),
                UserId = userId
            };
        }

        private async Task DeleteFileFromAzure(string fileName)
        {
            try
            {
                var shareClient = new ShareClient(connectionString, shareName);
                var rootDir = shareClient.GetRootDirectoryClient();
                var fileClient = rootDir.GetFileClient(fileName);

                await fileClient.DeleteIfExistsAsync();
            }
            catch (Exception ex)
            {
                // Log the error but don't throw - we still want to delete from database
                Console.WriteLine($"Error deleting file from Azure: {ex.Message}");
            }
        }
    }
}