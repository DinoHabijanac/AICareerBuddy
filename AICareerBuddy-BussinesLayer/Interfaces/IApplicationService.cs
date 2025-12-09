using AICareerBuddy_Entities.Entities;

namespace AI_CareerBuddy_Backend.Controllers
{
    public interface IApplicationService
    {
        public Task<List<Application>> GetApplications();
        public Task<Application> GetApplicationById(int id);
        public Task<Application> GetApplicationByStudentId(int studentId);
        public Task<bool> PostApplication(Application jobApplication);
        public Task<bool> PutApplication(Application jobApplication);
        public Task<bool> DeleteApplication(int id);
        public Task<bool> DeleteApplication(string applicationName);
    }
}
