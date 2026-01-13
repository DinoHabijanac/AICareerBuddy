using AICareerBuddy_BussinesLayer.Interfaces;
using AICareerBuddy_DataAccessLayer.Repositories;
using AICareerBuddy_Entities.Entities;
using Microsoft.EntityFrameworkCore;
using Microsoft.VisualBasic;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace AICareerBuddy_BussinesLayer.Services
{
    public class JobService : IJobService
    {
        private readonly JobRepository Repository;

        public JobService()
        {
            Repository = new JobRepository();
        }

        public async Task<JobListing> GetJob(int id)
        {
            return await Repository.GetJob(id).FirstAsync();
        }

        public async Task<JobListing> GetJob(string jobName)
        {
            return await Repository.GetJob(jobName).FirstAsync();
        }
        public async Task<List<JobListing>> GetJobs()
        {
            return await Repository.GetAll().ToListAsync();
        }
        public async Task<bool> PostJob(JobListing jobListing)
        {
            var result = await Repository.Add(jobListing);
            if (result == 1) return true;
            else return false;
        }

        public async Task<bool> PutJob(JobListing jobListing)
        {
            var result = await Repository.Update(jobListing);        
            if(result==1) return true;
            else return false;
        }
        public async Task<bool> DeleteJob(int id)
        {
            var job = Repository.GetJob(id).FirstOrDefault();
            int result;
            if (job != null) result = await Repository.Remove(job);
            else result = 0;


            if (result == 1) return true;
            else return false;
        }
        public async Task<bool> DeleteJob(string jobName)
        {
            var job = Repository.GetJob(jobName).FirstOrDefault();
            var result = await Repository.Remove(job);
            if (result == 1) return true;
            else return false;
        }

        public async Task<User> GetStudentById(int id)
        {
            return await Repository.GetStudent(id).FirstAsync();
        }
    }
}
