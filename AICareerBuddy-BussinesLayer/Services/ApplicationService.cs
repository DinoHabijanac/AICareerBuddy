using AI_CareerBuddy_Backend.Controllers;
using AICareerBuddy_DataAccessLayer.Repositories;
using AICareerBuddy_Entities.Entities;
using Microsoft.EntityFrameworkCore;
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

        public ApplicationService()
        {
            Repository = new ApplicationRepository();
        }

        public async Task<JobApplication> GetApplicationById(int id)
        {
            return await Repository.GetApplicationById(id).FirstOrDefaultAsync();
        }

        public async Task<List<JobApplication>> GetApplicationsByStudentId(int studentId)
        {
            return await Repository.GetApplicationsByStudentId(studentId).ToListAsync();
        }

        public async Task<List<JobApplication>> GetApplications()
        {
            return await Repository.GetAll().ToListAsync();
        }

        public async Task<bool> PostApplication(JobApplication jobApplication)
        {
            var result = await Repository.Add(jobApplication);
            if (result == 1) return true;
            else return false;
        }

        public async Task<bool> PutApplication(JobApplication jobApplication)
        {
            var result = await Repository.Update(jobApplication);
            if (result == 1) return true;
            else return false;
        }
        public async Task<bool> DeleteApplication(int id)
        {
            var job = Repository.GetApplicationById(id).FirstOrDefault();
            int result;
            if (job != null) result = await Repository.Remove(job);
            else result = 0;


            if (result == 1) return true;
            else return false;
        }
    }
}
