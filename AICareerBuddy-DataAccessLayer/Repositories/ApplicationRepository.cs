using AICareerBuddy_Entities.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace AICareerBuddy_DataAccessLayer.Repositories
{
    public class ApplicationRepository : Repository<JobApplication>
    {
        public ApplicationRepository() : base(new AIR_projektContext()) { }

        public IQueryable<JobApplication> GetApplicationById(int id)
        {
            var query = from j in Entities where j.Id == id select j;
            return query;
        }

        public IQueryable<JobApplication> GetApplicationsByStudentId(int studentId)
        {
            var query = from j in Entities where j.StudentId == studentId select j;
            return query;
        }
        public IQueryable<JobApplication> GetApplicationsByJobId(int jobId)
        {
            var query = from j in Entities where j.JobId == jobId select j;
            return query;
        }
        public IQueryable<JobApplication> GetApplicationsByEmployerId(int employerId)
        {
            var query = from j in Entities where j.EmployerId == employerId select j;
            return query;
        }
        public override async Task<int> Update(JobApplication entity, bool saveChanges = true)
        {
            var application = GetApplicationById(entity.Id).FirstOrDefault();
            if(application != null)
            {
                // application.StudentId = entity.StudentId;
                // application.JobId = entity.JobId;
                application.Status = entity.Status;
                application.DateOfSubmission = entity.DateOfSubmission;
                application.WorkExperience = entity.WorkExperience;
                application.ExpectedPay = entity.ExpectedPay;

                return await Context.SaveChangesAsync();
            }
            else
            {
                throw new ArgumentNullException("Job application sent was null");
            }
        }
    }
}
