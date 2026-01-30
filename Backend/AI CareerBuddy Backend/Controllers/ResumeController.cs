using AICareerBuddy_BussinesLayer.Interfaces;
using AICareerBuddy_BussinesLogic.Services;
using AICareerBuddy_Entities.Entities;
using Microsoft.AspNetCore.Mvc;
using System.Threading.Tasks;

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

        [HttpGet("GetResume/{id}")]
        public async Task<ActionResult<ResumeFileInfo>> Get(int id)
        {
            try
            {
                return await ResumeService.GetResume(id);
            }
            catch
            {
                return NotFound();
            }
        }

        [HttpPost(Name = "PostResume")]
        [Consumes("multipart/form-data")]
        public async Task<IActionResult> Post(IFormFile file, [FromForm] int userId)
        {
            try
            {
                var fileInfo = await ResumeService.PostResume(file, userId);
                return Ok(fileInfo);
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpGet("AnalyzeAI/{userId}")]
        public async Task<IActionResult> GetResumeAnalysisAI(int userId)
        {
            try
            {
                var aiFeedback = await ResumeService.GetResumeAnalysisAI(userId);
                return Ok(aiFeedback);
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message + ex.InnerException?.Message);
            }
        }


        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            try
            {
                var deleted = await ResumeService.DeleteResume(id);

                if (deleted)
                {
                    return NoContent(); //204 - uspješno obrisano
                }
                else
                {
                    return NotFound($"Resume for user {id} not found"); //404 - nije pronađeno
                }
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, $"Error deleting resume for user {id}");
                return BadRequest(ex.Message);
            }
        }

        [HttpPut("{id}")]
        [Consumes("multipart/form-data")]
        public async Task<IActionResult> Update(int id, IFormFile file)
        {
            try
            {
                var fileInfo = await ResumeService.UpdateResume(file, id);

                if (fileInfo != null)
                {
                    return Ok(fileInfo);
                }
                else
                {
                    return BadRequest("Failed to update resume");
                }
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, $"Error updating resume for user {id}");
                return BadRequest(ex.Message);
            }
        }
    }
}