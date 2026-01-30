using AICareerBuddy_Entities.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace AICareerBuddy_BussinesLayer.Interfaces
{
    public interface IJobService
    {
        public Task<List<JobListing>> GetJobs();
        public Task<JobListing> GetJob(int id);
        public Task<JobListing> GetJob(string jobName);
        public Task<bool> PostJob(JobListing jobListing);
        public Task<bool> PutJob(JobListing jobListing);
        public Task<bool> DeleteJob(int id);
        public Task<bool> DeleteJob(string jobName);
        public Task<User> GetStudentById(int id);
        public Task<List<JobListing>> GetJobsByUserId(int userId);
    }
}
