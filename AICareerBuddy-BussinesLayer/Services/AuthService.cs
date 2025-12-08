using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;
using Microsoft.AspNetCore.Identity;     //
using AICareerBuddy_DataAccessLayer.Models;
using AICareerBuddy_BussinesLogicLayer.Interfaces;

namespace AICareerBuddy_BussinesLogic.Services
{
    public class AuthService : IAuthService
    {
        private readonly AIR_projektContext _context;
        private readonly PasswordHasher<User> _passwordHasher = new PasswordHasher<User>();

        public AuthService(AIR_projektContext context)
        {
            _context = context;
        }

        public async Task<User?> AuthenticateAsync(string username, string password)
        {
            // 1. nađi usera po usernameu
            var user = await _context.Users
                .FirstOrDefaultAsync(u => u.Username == username);

            if (user == null)
                return null;

            // 2. pokušaj provjeriti kao HASH (za nove korisnike)
            var verifyResult = _passwordHasher.VerifyHashedPassword(
                user,           // može biti i null, ali nije bitno
                user.Password,  // što je spremljeno u bazi
                password        // lozinka koju je korisnik poslao
            );

            if (verifyResult == PasswordVerificationResult.Success)
            {
                return user;    // lozinka je točna (hashirani korisnik)
            }

            // 3. Fallback: podrži stare korisnike s plain-text lozinkom
            if (user.Password == password)
            {
                return user;
            }

            // 4. ni hash ni plain nisu prošli → krivi kredencijali
            return null;
        }
    }
}
