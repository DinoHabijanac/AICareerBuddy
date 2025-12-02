using AICareerBuddy_Entities.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using System.Threading.Tasks;

namespace AICareerBuddy_DataAccessLayer.Repositories
{
    public class JobRepository : Repository<JobListing>
    {
        public JobRepository() : base(new AIR_projektContext()) { } 

        public IQueryable<JobListing> GetJob(string jobName)
        {
            var query = from j in Entities where j.Name == jobName select j;
            return query;   
        }
        public IQueryable<JobListing> GetJob(int id)
        {
            var query = from j in Entities where j.Id == id select j;
            return query;
        }

        public override async Task<int> Update(JobListing entity, bool saveChanges = true)
        {
            var job = GetJob(entity.Id).FirstOrDefault();
            if (job != null) 
            { 
                job.Name = entity.Name;
                job.Description = entity.Description;
                job.Category = entity.Category;
                job.Location = entity.Location;
                job.ListingExpires = entity.ListingExpires;
                job.Terms = entity.Terms;
                job.PayPerHour = entity.PayPerHour;

                return await Context.SaveChangesAsync();
            }
            else
            {
                throw new ArgumentNullException("JobListing sent was null");
            }
            
        }
    }
}
