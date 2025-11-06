using AICareerBuddy_Entities.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using AICareerBuddy_DataAccessLayer.Repositories;

namespace AICareerBuddy_DataAccessLayer.Services
{
    public class ResumeService
    {
        public static List<Resume> GetResumes()
        {
            //implementiraj

            return ResumeRepo.GetResumes().ToList();
        }
    }
}
