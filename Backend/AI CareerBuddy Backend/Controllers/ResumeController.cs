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
        [Consumes ("multipart/form-data")]
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
    }
}