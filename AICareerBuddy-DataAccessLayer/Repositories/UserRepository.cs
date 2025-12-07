// AICareerBuddy-DataAccessLayer/Repositories/UserRepo.cs
using AICareerBuddy_Entities.Entities;
using System.Collections.Generic;
using System.Linq;

namespace AICareerBuddy_DataAccessLayer.Repositories
{
    public static class UserRepository
    {
        // In-memory "baza" korisnika
        private static List<User> _users = new List<User>();

        public static User? GetUserByEmail(string email)
        {
            return _users.FirstOrDefault(u => u.Email.ToLower() == email.ToLower());
        }

        // **NOVO**: Dohvat korisnika po korisničkom imenu
        public static User? GetUserByUsername(string username)
        {
            return _users.FirstOrDefault(u => u.Username.ToLower() == username.ToLower());
        }

        public static User Add(User user)
        {
            // Dodijeli ID (npr. auto-increment simulacija)
            user.Id = _users.Count + 1;
            _users.Add(user);
            return user;
        }
    }
}
