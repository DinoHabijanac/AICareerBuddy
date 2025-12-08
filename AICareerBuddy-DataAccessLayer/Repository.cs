using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using AICareerBuddy_Entities.Entities;
using Microsoft.EntityFrameworkCore;

namespace AICareerBuddy_DataAccessLayer
{
    /// <summary>
    /// Generički repozitorij za rad s EF Core DbContextom.
    /// Ovo je analogan patternu iz dev-backend projekta, ali prilagođen DI pristupu.
    /// </summary>
    public abstract class Repository<T> : IDisposable where T : class
    {
        protected readonly AIR_projektContext Context;
        protected readonly DbSet<T> Entities;

        protected Repository(AIR_projektContext context)
        {
            Context = context;
            Entities = Context.Set<T>();
        }

        /// <summary>
        /// Bazni IQueryable upit nad entitetom.
        /// </summary>
        public virtual IQueryable<T> Query()
        {
            return Entities.AsQueryable();
        }

        public virtual async Task AddAsync(T entity, CancellationToken cancellationToken = default)
        {
            if (entity == null) throw new ArgumentNullException(nameof(entity));
            await Entities.AddAsync(entity, cancellationToken);
        }

        public virtual void Update(T entity)
        {
            if (entity == null) throw new ArgumentNullException(nameof(entity));
            Entities.Update(entity);
        }

        public virtual void Remove(T entity)
        {
            if (entity == null) throw new ArgumentNullException(nameof(entity));
            Entities.Remove(entity);
        }

        public virtual Task<int> SaveChangesAsync(CancellationToken cancellationToken = default)
        {
            return Context.SaveChangesAsync(cancellationToken);
        }

        public void Dispose()
        {
            Context.Dispose();
            GC.SuppressFinalize(this);
        }
    }
}
