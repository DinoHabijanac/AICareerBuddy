using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using AICareerBuddy_Entities.Entities;

namespace AI_CareerBuddy_APILayer.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class DebugDbController : ControllerBase
    {
        private readonly AIR_projektContext _context;

        public DebugDbController(AIR_projektContext context)
        {
            _context = context;
        }

        [HttpGet("test")]
        public async Task<IActionResult> TestConnection()
        {
            var userCount = await _context.Users.CountAsync();
            var jobCount = await _context.JobListings.CountAsync();
            var cvCount = await _context.ResumeFileInfos.CountAsync();

            return Ok(new
            {
                Users = userCount,
                JobListings = jobCount,
                ResumeFileInfos = cvCount
            });
        }
    }
}
