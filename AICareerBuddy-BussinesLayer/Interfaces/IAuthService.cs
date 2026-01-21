using System.Threading.Tasks;
using AICareerBuddy_Entities.Entities;

namespace AICareerBuddy_BussinesLogicLayer.Interfaces
{
    public interface IAuthService
    {
        Task<User?> AuthenticateAsync(string username, string password);
        Task<User?> UserExists(string username);
    }
}
