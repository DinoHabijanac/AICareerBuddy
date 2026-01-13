using AICareerBuddy_BussinesLogic.Services;
using Microsoft.AspNetCore.Mvc;

namespace AI_CareerBuddy_Backend.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class RegistrationController : ControllerBase
    {
        private readonly RegistrationService _registrationService;
        private readonly ILogger<RegistrationController> _logger;

        public RegistrationController(RegistrationService registrationService, ILogger<RegistrationController> logger)
        {
            _registrationService = registrationService;
            _logger = logger;
        }

        [HttpPost("register")]
        public async Task<IActionResult> Register([FromBody] RegistrationRequestDto request)
        {
            try
            {
                var responseDto = await _registrationService.RegisterUserAsync(request);
                return Ok(responseDto);
            }
            catch (ArgumentException ex)
            {
                // Validacijska greška ili duplicirani email
                _logger.LogWarning(ex, "Registration validation error.");
                return BadRequest(ex.Message);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Unexpected error during registration.");
                return StatusCode(500, "Internal server error");
            }
        }
    }
}
