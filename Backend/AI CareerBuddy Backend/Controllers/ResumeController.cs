using AICareerBuddy_DataAccessLayer.Services;
using AICareerBuddy_Entities.Entities;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;

namespace AI_CareerBuddy_Backend.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class ResumeController : ControllerBase
    {
        
        private readonly ILogger<ResumeController> _logger;

        public ResumeController(ILogger<ResumeController> logger)
        {
            _logger = logger;
        }

        [HttpGet(Name = "GetResumes")]
        public IEnumerable<Resume> Get()
        {
            return ResumeService.GetResumes().ToList();
        }
    }
}
