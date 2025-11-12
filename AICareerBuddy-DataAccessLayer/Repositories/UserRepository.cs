using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using AICareerBuddy_Entities.Entities;

namespace AICareerBuddy_DataAccessLayer.Repositories
{
    // Demo in-memory repo; kasnije EF Core
    public class UserRepository
    {
        private readonly List<User> _users = new()
        {
            new User { Id = 1, Username = "test",  Password = "1234", Email = "test@mail.com"  },
            new User { Id = 2, Username = "admin", Password = "admin", Email = "admin@mail.com" }
        };

        public User? GetByCredentials(string username, string password)
            => _users.FirstOrDefault(u =>
                u.Username.Equals(username, StringComparison.OrdinalIgnoreCase) &&
                u.Password == password);
    }
}

