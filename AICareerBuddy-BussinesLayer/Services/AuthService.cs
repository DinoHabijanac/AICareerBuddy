using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;
using AICareerBuddy_DataAccessLayer.Models;
using AICareerBuddy_BussinesLogicLayer.Interfaces;

namespace AICareerBuddy_BussinesLogic.Services   
{
    public class AuthService : IAuthService
    {
        private readonly AIR_projektContext _context;

        public AuthService(AIR_projektContext context)
        {
            _context = context;
        }

        public async Task<User?> AuthenticateAsync(string username, string password)
        {
            return await _context.Users
                .FirstOrDefaultAsync(u => u.Username == username && u.Password == password);
        }
    }
}
