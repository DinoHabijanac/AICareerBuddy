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
        public List<JobListing> GetJobs();
        public JobListing GetJob(int id);
        public JobListing GetJob(string jobName);
        public JobListing PostJob(JobListing jobListing);
        public JobListing PutJob(JobListing jobListing);
        public bool DeleteJob(int id);
        public bool DeleteJob(string jobName);
    }
}
