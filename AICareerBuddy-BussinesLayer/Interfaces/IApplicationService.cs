using AICareerBuddy_Entities.Entities;

namespace AICareerBuddy_BussinesLayer.Interfaces
{
    public interface IApplicationService
    {
        public Task<List<JobApplication>> GetApplications();
        public Task<JobApplication> GetApplicationById(int id);
        public Task<List<JobApplication>> GetApplicationsByStudentId(int studentId);
        public Task<bool> PostApplication(JobApplication jobApplication);
        public Task<bool> PutApplication(JobApplication jobApplication);
        public Task<bool> DeleteApplication(int id);
    }
}