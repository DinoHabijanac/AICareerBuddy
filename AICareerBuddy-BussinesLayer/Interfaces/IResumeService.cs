using AICareerBuddy_Entities.Entities;
using Microsoft.AspNetCore.Http;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace AICareerBuddy_BussinesLayer.Interfaces
{
    public interface IResumeService
    {
        public Task<List<ResumeFileInfo>> GetResumes();
        public Task<ResumeFileInfo> GetResume(int id);
        public Task<ResumeFileInfo> GetResumeByUserId(int userId);
        public Task<ResumeFileInfo> PostResume(IFormFile file, int userId);
        public Task<ResumeFileInfo> UpdateResume(int id, IFormFile file, int userId);
        public Task<bool> DeleteResume(int id, int userId);
    }
}