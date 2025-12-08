using System.Threading.Tasks;
using AICareerBuddy_DataAccessLayer.Models;

namespace AICareerBuddy_BussinesLogicLayer.Interfaces
{
    public interface IAuthService
    {
        Task<User?> AuthenticateAsync(string username, string password);
    }
}
