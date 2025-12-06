using AICareerBuddy_BussinesLogic.Services;
using AICareerBuddy_Entities.Entities;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
// Backend/AI CareerBuddy Backend/Controllers/AuthController.cs

namespace AI_CareerBuddy_Backend.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class AuthController : ControllerBase
    {
        private readonly RegistrationService _registrationService;
        public AuthController(RegistrationService registrationService)
        {
            _registrationService = registrationService;
        }

        [HttpPost("register")]
        public IActionResult Register([FromBody] RegistrationRequestDto request)
        {
            try
            {
                var responseDto = _registrationService.RegisterUser(request);
                return Ok(responseDto);
            }
            catch (ArgumentException ex)
            {
                // Validation or duplicate error
                return BadRequest(ex.Message);
            }
            catch (Exception)
            {
                return StatusCode(500, "Internal server error");
            }
        }
    }
}
