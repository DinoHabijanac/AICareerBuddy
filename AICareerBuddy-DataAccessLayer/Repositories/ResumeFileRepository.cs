using AICareerBuddy_Entities.Entities;
using Microsoft.EntityFrameworkCore;

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

        public override async Task<int> Update(ResumeFileInfo entity, bool saveChanges = true)
        {
            try
            {
                var existingEntity = await Entities.FindAsync(entity.Id);
                if (existingEntity == null)
                {
                    throw new KeyNotFoundException($"Resume with ID {entity.Id} not found");
                }

                // Update properties
                Context.Entry(existingEntity).CurrentValues.SetValues(entity);

                if (saveChanges)
                {
                    return await Context.SaveChangesAsync();
                }

                return 0;
            }
            catch (Exception ex)
            {
                throw new InvalidOperationException($"Error updating resume: {ex.Message}");
            }
        }
    }
}