using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using AICareerBuddy_Entities.Entities;
using Microsoft.EntityFrameworkCore;
using System.Threading.Tasks;

namespace AICareerBuddy_DataAccessLayer
{
    public abstract class Repository<T> : IDisposable where T : class
    {
        public AIR_projektContext Context { get; set; } 
        public DbSet<T> Entities { get; set; }
        protected Repository(AIR_projektContext context)
        {
            Context = context;
            Entities = Context.Set<T>();
        }

        public virtual IQueryable<T> GetAll()
        {
            var query = from s in Entities
                        select s;
            return query;
        }

        public virtual async Task<int> Add(T entity, bool saveChanges = true)
        {
            Entities.Add(entity);
            if (saveChanges)
            {
                return await SaveChangesAsync();
            }
            else
            {
                return 0;
            }
        }

        public abstract Task<int> Update(T entity, bool saveChanges = true);

        public virtual async Task<int> Remove(T entity, bool saveChanges = true)
        {
            Entities.Attach(entity);
            Entities.Remove(entity);
            if (saveChanges)
            {
                return await Context.SaveChangesAsync();
            }
            else
            {
                return 0;
            }
        }

        public async virtual void Dispose()
        {
            await Context.DisposeAsync();
            GC.SuppressFinalize(this);
        }

        public async virtual Task<int> SaveChangesAsync()
        {
            return await Context.SaveChangesAsync();
        }
    }
}
