using AI_CareerBuddy_Backend.Controllers;
using AICareerBuddy_DataAccessLayer.Repositories;
using AICareerBuddy_Entities.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace AICareerBuddy_BussinesLayer.Services
{
    public class ApplicationService : IApplicationService
    {
        private readonly ApplicationRepository Repository;

        public Task<bool> DeleteApplication(int id)
        {
            throw new NotImplementedException();
        }

        public Task<bool> DeleteApplication(string applicationName)
        {
            throw new NotImplementedException();
        }

        public Task<Application> GetApplicationById(int id)
        {
            throw new NotImplementedException();
        }

        public Task<Application> GetApplicationByStudentId(int studentId)
        {
            throw new NotImplementedException();
        }

        public Task<List<Application>> GetApplications()
        {
            throw new NotImplementedException();
        }

        public Task<bool> PostApplication(Application jobApplication)
        {
            throw new NotImplementedException();
        }

        public Task<bool> PutApplication(Application jobApplication)
        {
            throw new NotImplementedException();
        }
    }
}
