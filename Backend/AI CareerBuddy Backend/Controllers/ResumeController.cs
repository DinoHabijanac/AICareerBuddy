using AICareerBuddy_BussinesLayer.Interfaces;
using AICareerBuddy_Entities.Entities;
using Microsoft.AspNetCore.Mvc;

namespace AI_CareerBuddy_Backend.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class ResumeController : ControllerBase
    {
        private readonly ILogger<ResumeController> _logger;
        private readonly IResumeService ResumeService;

        public ResumeController(ILogger<ResumeController> logger, IResumeService resumeService)
        {
            _logger = logger;
            ResumeService = resumeService;
        }

        [HttpGet(Name = "GetResumes")]
        public async Task<IEnumerable<ResumeFileInfo>> GetResumes()
        {
            return await ResumeService.GetResumes();
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<ResumeFileInfo>> Get(int id)
        {
            try
            {
                var resume = await ResumeService.GetResume(id);
                if (resume == null)
                {
                    return NotFound(new { message = "Resume not found" });
                }
                return Ok(resume);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error retrieving resume");
                return StatusCode(500, new { message = "Internal server error" });
            }
        }

        [HttpGet("user/{userId}")]
        public async Task<ActionResult<ResumeFileInfo>> GetByUserId(int userId)
        {
            try
            {
                var resume = await ResumeService.GetResumeByUserId(userId);
                if (resume == null)
                {
                    return NotFound(new { message = "Resume not found for this user" });
                }
                return Ok(resume);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error retrieving resume for user");
                return StatusCode(500, new { message = "Internal server error" });
            }
        }

        [HttpPost(Name = "PostResume")]
        [Consumes("multipart/form-data")]
        public async Task<IActionResult> Post(IFormFile file, [FromForm] int userId)
        {
            try
            {
                // First check if user already has a resume
                var existingResume = await ResumeService.GetResumeByUserId(userId);
                if (existingResume != null)
                {
                    return Conflict(new { message = "Korisnik već ima učitan životopis. Koristite opciju ažuriranja." });
                }

                var fileInfo = await ResumeService.PostResume(file, userId);
                // Return just the data, not wrapped in an object
                return Ok(fileInfo);
            }
            catch (InvalidOperationException ex) when (ex.Message.Contains("already has a resume"))
            {
                return Conflict(new { message = "Korisnik već ima učitan životopis. Koristite opciju ažuriranja." });
            }
            catch (ArgumentNullException ex)
            {
                return BadRequest(new { message = ex.Message });
            }
            catch (FormatException ex)
            {
                return BadRequest(new { message = ex.Message });
            }
            catch (ArgumentOutOfRangeException ex)
            {
                return BadRequest(new { message = ex.Message });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error uploading resume");
                return StatusCode(500, new { message = "Greška pri učitavanju životopisa" });
            }
        }

        [HttpPut("{id}")]
        [Consumes("multipart/form-data")]
        public async Task<IActionResult> Update(int id, IFormFile file, [FromForm] int userId)
        {
            try
            {
                var fileInfo = await ResumeService.UpdateResume(id, file, userId);
                // Return just the data, not wrapped in an object
                return Ok(fileInfo);
            }
            catch (KeyNotFoundException ex)
            {
                return NotFound(new { message = ex.Message });
            }
            catch (ArgumentNullException ex)
            {
                return BadRequest(new { message = ex.Message });
            }
            catch (FormatException ex)
            {
                return BadRequest(new { message = ex.Message });
            }
            catch (ArgumentOutOfRangeException ex)
            {
                return BadRequest(new { message = ex.Message });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error updating resume");
                return StatusCode(500, new { message = "Greška pri ažuriranju životopisa" });
            }
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(int id, [FromQuery] int userId)
        {
            try
            {
                var result = await ResumeService.DeleteResume(id, userId);
                if (result)
                {
                    return Ok(new { message = "Životopis je izbrisan" });
                }
                else
                {
                    return StatusCode(500, new { message = "Greška pri brisanju životopisa" });
                }
            }
            catch (KeyNotFoundException ex)
            {
                return NotFound(new { message = ex.Message });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error deleting resume");
                return StatusCode(500, new { message = "Greška pri brisanju životopisa" });
            }
        }
    }
}