using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using AICareerBuddy_DataAccessLayer.Repositories;
using AICareerBuddy_Entities.Entities;

namespace AICareerBuddy_BusinessLogicLayer.Services
{
    public class AuthService
    {
        private readonly UserRepository _users = new();

        public (bool Success, string Message, User? User) Login(string username, string password)
        {
            if (string.IsNullOrWhiteSpace(username) || string.IsNullOrWhiteSpace(password))
                return (false, "Polja ne smiju biti prazna.", null);

            var user = _users.GetByCredentials(username, password);
            if (user is null)
                return (false, "Neispravni korisnički podaci.", null);

            return (true, "Prijava uspješna.", user);
        }
    }
}

