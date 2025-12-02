using AICareerBuddy_Entities.Entities;

namespace AICareerBuddy_DataAccessLayer.Repositories
{
    public class ResumeFileRepository : Repository<ResumeFileInfo>
    {
        public ResumeFileRepository() : base(new AIR_projektContext()) { }
        public IQueryable<ResumeFileInfo> GetResume(int userId)
        {
            var query = from r in Entities where r.UserId == userId select r;
            return query;
        }

        public override Task<int> Update(ResumeFileInfo entity, bool saveChanges = true)
        {
            throw new NotImplementedException();
        }
    }
}
