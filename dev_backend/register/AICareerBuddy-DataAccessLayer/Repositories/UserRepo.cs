// AICareerBuddy-DataAccessLayer/Repositories/UserRepo.cs
using AICareerBuddy_Entities.Entities;
using System.Collections.Generic;
using System.Linq;

namespace AICareerBuddy_DataAccessLayer.Repositories
{
    public static class UserRepo
    {
        // In-memory user list (simulate database)
        private static List<User> _users = new List<User>();

        public static User? GetUserByEmail(string email)
        {
            return _users.FirstOrDefault(u => u.Email.ToLower() == email.ToLower());
        }
        public static User? GetUserByUsername(string username)
        {
            return _users.FirstOrDefault(u => u.Username.ToLower() == username.ToLower());
        }
        public static User? GetUserById(int id)
        {
            return _users.FirstOrDefault(u => u.Id == id);
        }
        public static IEnumerable<User> GetUsers()
        {
            return _users.AsEnumerable();
        }
        public static User CreateUser(User user)
        {
            // assign new Id
            int newId = _users.Count > 0 ? _users.Max(u => u.Id) + 1 : 1;
            user.Id = newId;
            _users.Add(user);
            return user;
        }
    }
}
