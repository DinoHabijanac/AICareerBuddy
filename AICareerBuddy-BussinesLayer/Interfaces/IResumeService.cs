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
        Task<List<ResumeFileInfo>> GetResumes();
        Task<ResumeFileInfo> GetResume(int id);
        Task<ResumeFileInfo> PostResume(IFormFile file, int userId);
        Task<bool> DeleteResume(int userId);
        Task<ResumeFileInfo> UpdateResume(IFormFile file, int userId);
    }
}