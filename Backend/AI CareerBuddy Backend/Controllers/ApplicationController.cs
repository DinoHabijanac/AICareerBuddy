using AICareerBuddy_BussinesLayer.Interfaces;
using AICareerBuddy_Entities.Entities;
using Microsoft.AspNetCore.Mvc;

// For more information on enabling Web API for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace AI_CareerBuddy_Backend.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class ApplicationController : ControllerBase
    {
        private readonly IApplicationService ApplicationService;

        public ApplicationController(IApplicationService applicationService)
        {
            ApplicationService = applicationService;
        }

        // GET: api/<ApplicationController>
        [HttpGet]
        public async Task<ActionResult<IEnumerable<JobApplication>>> Get()
        {
            var applications = await ApplicationService.GetApplications();
            if (applications != null) return Ok(applications);
            else return NotFound();
        }

        [HttpGet("student")]
        public async Task<ActionResult<IEnumerable<JobApplication>>> GetByStudentId([FromQuery] int studentId)
        {
            var applications = await ApplicationService.GetApplicationsByStudentId(studentId);
            if (applications != null) return Ok(applications);
            else return NotFound();
        }

        [HttpGet("employer")]
        public async Task<ActionResult<IEnumerable<JobApplication>>> GetByEmployerId([FromQuery] int employerId)
        {
            var applications = await ApplicationService.GetApplicationsByEmployerId(employerId);
            if (applications != null) return Ok(applications);
            else return NotFound();
        }

        [HttpGet("job")]
        public async Task<ActionResult<IEnumerable<JobApplication>>> GetByJobId([FromQuery] int jobId)
        {
            var applications = await ApplicationService.GetApplicationsByJobId(jobId);
            if (applications != null) return Ok(applications);
            else return NotFound();
        }

        // GET api/<ApplicationController>/5
        [HttpGet("id")]
        public async Task<ActionResult<JobApplication>> Get([FromQuery] int id)
        {
            var application = await ApplicationService.GetApplicationById(id);
            if (application != null) return Ok(application);
            else return NotFound();
        }

        // POST api/<ApplicationController>
        [HttpPost]
        public async Task<ActionResult<JobApplication>> Post([FromBody] JobApplication application)
        {
            var isCreated = await ApplicationService.PostApplication(application);
            if (isCreated) return Created();
            else return BadRequest();
        }

        // PUT api/<ApplicationController>/5
        [HttpPut("{id}")]
        public async Task<ActionResult<JobApplication>> Put(int id, [FromBody] JobApplication application)
        {
            if (application == null) return BadRequest("PReLoše");
            if (application.Id != id) return BadRequest("Loše");
            var updated = await ApplicationService.PutApplication(application);
            if (updated) return Ok(new APIResponse { success = true, message = "Uspješna promjena" });
            else return NotFound();
        }

        // DELETE api/<ApplicationController>/5
        [HttpDelete("{id}")]
        public async Task<ActionResult<JobApplication>> Delete(int id)
        {
            var deleted = await ApplicationService.DeleteApplication(id);
            if (deleted) return NoContent();
            else return NotFound();
        }
    }
}
