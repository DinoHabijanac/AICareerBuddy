using AICareerBuddy_Entities.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace AICareerBuddy_DataAccessLayer.Repositories
{
    public class ApplicationRepository : Repository<Application>
    {
        public ApplicationRepository() : base(new AIR_projektContext()) { }

        public override Task<int> Update(Application entity, bool saveChanges = true)
        {
            throw new NotImplementedException();
        }
    }
}
