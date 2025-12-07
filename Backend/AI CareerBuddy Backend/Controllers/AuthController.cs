// Backend/AI CareerBuddy Backend/Controllers/AuthController.cs
using AICareerBuddy_BussinesLogic.Services;
using AICareerBuddy_Entities.Entities;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;

namespace AI_CareerBuddy_Backend.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class AuthController : ControllerBase
    {
        private readonly AuthService _authService;
        public AuthController(AuthService authService)
        {
            _authService = authService;
        }

        [HttpPost("register")]
        public IActionResult Register([FromBody] RegistrationRequestDto request)
        {
            try
            {
                var responseDto = _authService.RegisterUser(request);
                return Ok(responseDto);
            } catch (ArgumentException ex)
            {
                // Validation or duplicate error (email already exists, etc.)
                return BadRequest(ex.Message);
            } catch (Exception)
            {
                return StatusCode(500, "Internal server error");
            }
        }

        [HttpPost("login")]
        public IActionResult Login([FromBody] LoginRequestDto request)
        {
            try
            {
                var responseDto = _authService.LoginUser(request);
                return Ok(responseDto);
            } catch (ArgumentException ex)
            {
                // Invalid credentials or validation error
                return BadRequest(ex.Message);
            } catch (Exception)
            {
                return StatusCode(500, "Internal server error");
            }
        }
    }
}
