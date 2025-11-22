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
        public List<Resume> GetResumes();
        public Resume GetResume(int id);
        public Task<FilesInfo> PostResume(IFormFile file);
    }
}