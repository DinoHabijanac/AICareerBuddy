using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using AICareerBuddy_Entities.Entities;
using Microsoft.EntityFrameworkCore;

namespace AICareerBuddy_DataAccessLayer.Repositories
{
    public interface IUserRepository
    {
        Task<User?> GetUserByEmailAsync(string email);
        Task<bool> EmailExistsAsync(string email);
        Task<User> CreateUserAsync(User user);
    }

    /// <summary>
    /// EF Core repozitorij za korisnike.
    /// </summary>
    public class UserRepository : Repository<User>, IUserRepository
    {
        public UserRepository(AIR_projektContext context) : base(context)
        {
        }

        public async Task<User?> GetUserByEmailAsync(string email)
        {
            if (string.IsNullOrWhiteSpace(email))
                throw new ArgumentException("Email is required.", nameof(email));

            var normalized = email.Trim().ToLower();
            return await Entities.FirstOrDefaultAsync(u => u.Email.ToLower() == normalized);
        }

        public async Task<bool> EmailExistsAsync(string email)
        {
            if (string.IsNullOrWhiteSpace(email))
                return false;

            var normalized = email.Trim().ToLower();
            return await Entities.AnyAsync(u => u.Email.ToLower() == normalized);
        }

        public async Task<User> CreateUserAsync(User user)
        {
            if (user == null) throw new ArgumentNullException(nameof(user));

            await Entities.AddAsync(user);
            await Context.SaveChangesAsync();
            return user;
        }
    }
}
