using AI_CareerBuddy_Backend.DTOs;   // ovdje su LoginRequest i LoginResponse
using AICareerBuddy_BussinesLogic.Services;
using AICareerBuddy_BussinesLogicLayer.Interfaces;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.AspNetCore.Mvc;
using Microsoft.CodeAnalysis.CSharp.Syntax;
using System.Threading.Tasks;

namespace AI_CareerBuddy_Backend.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class AuthController : ControllerBase
    {
        private readonly IAuthService _authService;
        private readonly RegistrationService _registrationService;
      

        public AuthController(IAuthService authService, RegistrationService registrationService)
        {
            _authService = authService;
            _registrationService = registrationService;
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
                    user.Username,
                    user.Role
                }
            });
        }

        //login za prijavu sa Google-om
        [HttpPost("loginGoogle")]
        public async Task<IActionResult> LoginGoogle([FromBody] LoginRequest request) 
        {
            var user = await _authService.UserExists(request.Username);
            if (user == null)
            {
                return NotFound(new LoginResponse
                {
                    Success = false,
                    Message = "Taj korisnik ne postoji u bazi"
                });
            }
            else
            {
                return Ok(new LoginResponse
                {
                    Success = true,
                    Message = "Uspješna autentifikacija",
                    User = new
                    {
                        user.Id,
                        user.Username,
                        user.Role
                    }
                });
            }
        }

        //register sa google-om ako korisnik ne postoji u bazi
        [HttpPost("registerGoogle")]
        public async Task<IActionResult> RegisterGoogle([FromBody] RegistrationRequestDto request)
        {
            try
            {
                var response = await _registrationService.RegisterUserAsync(request);
                return Ok(response);
            }
            catch (Exception ex) 
            { 
                return BadRequest(ex.Message);
            }
        }
    }
}
