using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using AICareerBuddy_BussinesLogicLayer.Interfaces;
using AI_CareerBuddy_Backend.DTOs;   // ovdje su LoginRequest i LoginResponse

namespace AI_CareerBuddy_Backend.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class AuthController : ControllerBase
    {
        private readonly IAuthService _authService;

        public AuthController(IAuthService authService)
        {
            _authService = authService;
        }

        [HttpPost("login")]
        public async Task<IActionResult> Login([FromBody] LoginRequest request)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(new LoginResponse
                {
                    Success = false,
                    Message = "Neispravan format zahtjeva."
                });
            }

            var user = await _authService.AuthenticateAsync(request.Username, request.Password);

            if (user == null)
            {
                return Unauthorized(new LoginResponse
                {
                    Success = false,
                    Message = "Pogrešno korisnièko ime ili lozinka."
                });
            }

            // Za sada bez tokena – samo osnovni podaci
            return Ok(new LoginResponse
            {
                Success = true,
                Message = "Prijava uspješna.",
                Token = null,
                User = new
                {
                    user.Id,
                    user.Username
                }
            });
        }
    }
}
