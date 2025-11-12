using Microsoft.AspNetCore.Mvc;
using AICareerBuddy_BusinessLogicLayer.Services;
using AI_CareerBuddy_Backend.DTOs;

namespace AICareerBuddy_APILayer.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class AuthController : ControllerBase
    {
        private readonly AuthService _auth;

        public AuthController()
        {
            _auth = new AuthService();
        }

        [HttpPost("login")]
        public IActionResult Login([FromBody] LoginRequest request)
        {
            var (success, message, user) = _auth.Login(request.Username, request.Password);

            if (!success)
                return Unauthorized(new LoginResponse { Success = false, Message = message });

            // placeholder token; za JWT, zamijeniti
            var token = Guid.NewGuid().ToString();

            return Ok(new LoginResponse
            {
                Success = true,
                Message = message,
                Token = token,
                User = new { user!.Id, user.Username, user.Email }
            });
        }
    }
}

