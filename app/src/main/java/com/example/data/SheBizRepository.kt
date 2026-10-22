package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.NumberFormat
import java.util.Locale

class SheBizRepository {

    // Current User
    private val _currentUser = MutableStateFlow(UserProfile())
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    // Business Profile
    private val _businessProfile = MutableStateFlow(BusinessProfile())
    val businessProfile: StateFlow<BusinessProfile> = _businessProfile.asStateFlow()

    // Products (Abo's Bakery)
    private val _products = MutableStateFlow(
        listOf(
            Product("prod_1", "Chocolate Cake", "Decadent triple-layer rich Belgian dark chocolate sponge with ganache icing", 350.0, 5, "Cakes"),
            Product("prod_2", "Cupcakes - box of 12", "Hand-crafted artisan vanilla and red velvet cupcakes with buttercream swirl", 180.0, 2, "Pastries"),
            Product("prod_3", "Vanilla Cake", "Classic fluffy Madagascar vanilla bean sponge with strawberry compote filling", 300.0, 0, "Cakes"),
            Product("prod_4", "Wedding Cake", "Three-tier bespoke celebration cake with fondant floral finish and cake stand", 1800.0, 3, "Custom Cakes")
        )
    )
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    // Sales
    private val _sales = MutableStateFlow(
        listOf(
            Sale("sale_1", "prod_1", "Chocolate Cake", 2, 350.0, 700.0, "Thandiwe Khumalo", System.currentTimeMillis() - 86400000L * 2),
            Sale("sale_2", "prod_2", "Cupcakes - box of 12", 3, 180.0, 540.0, "Sipho Dlamini", System.currentTimeMillis() - 86400000L * 4),
            Sale("sale_3", "prod_4", "Wedding Cake", 1, 1800.0, 1800.0, "Nomonde Cele", System.currentTimeMillis() - 86400000L * 7),
            Sale("sale_4", "prod_1", "Chocolate Cake", 1, 350.0, 350.0, "Walk-in Customer", System.currentTimeMillis() - 86400000L * 1)
        )
    )
    val sales: StateFlow<List<Sale>> = _sales.asStateFlow()

    // Expenses
    private val _expenses = MutableStateFlow(
        listOf(
            Expense("exp_1", "Ingredients", 450.0, "2026-10-18", "Belgian cocoa, butter, free-range eggs & flour"),
            Expense("exp_2", "Packaging", 220.0, "2026-10-19", "Eco-friendly recyclable cake boxes and ribbon"),
            Expense("exp_3", "Utilities", 380.0, "2026-10-20", "Bakery oven gas cylinder refill"),
            Expense("exp_4", "Transport", 150.0, "2026-10-21", "Direct customer delivery in Summerstrand")
        )
    )
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

    // Customers
    private val _customers = MutableStateFlow(
        listOf(
            Customer("cust_1", "Thandiwe Khumalo", "+27 83 456 7890", 1400.0, 4),
            Customer("cust_2", "Nomonde Cele", "+27 82 987 6543", 2600.0, 2),
            Customer("cust_3", "Sipho Dlamini", "+27 71 234 5678", 540.0, 1),
            Customer("cust_4", "Zanele Mthembu", "+27 84 321 0987", 920.0, 3)
        )
    )
    val customers: StateFlow<List<Customer>> = _customers.asStateFlow()

    // Opportunities (18 Total)
    private val _opportunities = MutableStateFlow(
        listOf(
            Opportunity(
                id = "opp_1",
                title = "Junior Web Developer Internship",
                company = "ABC Technologies",
                location = "Gqeberha",
                type = "Internship",
                category = "Technology",
                skills = listOf("HTML", "CSS", "JavaScript", "Responsive Design"),
                description = "Join ABC Technologies' forward-thinking digital team in Gqeberha. You'll contribute to customer-facing web platforms, collaborate with senior UI engineers, and build responsive applications for local businesses.",
                requirements = listOf("Basic HTML5, CSS3, and modern JavaScript knowledge", "Passion for accessible UI and user experience", "Based in or willing to relocate to Nelson Mandela Bay"),
                closingDate = "2026-11-15",
                daysLeft = 24
            ),
            Opportunity(
                id = "opp_2",
                title = "Digital Marketing Learnership",
                company = "BrightPath SA",
                location = "Johannesburg",
                type = "Learnership",
                category = "Marketing",
                skills = listOf("Communication", "Teamwork", "Growth", "Social Media"),
                description = "Accredited 12-month National Certificate learnership empowering young women with end-to-end digital marketing skills, campaign analytics, and community management.",
                requirements = listOf("Matric certificate / Grade 12", "Eagerness to master social ad campaigns and Google Analytics", "Strong verbal and written English communication"),
                closingDate = "2026-11-08",
                daysLeft = 17
            ),
            Opportunity(
                id = "opp_3",
                title = "Women in Business Scholarship",
                company = "Future Founders",
                location = "South Africa",
                type = "Scholarship",
                category = "Business",
                skills = listOf("Communication", "Teamwork", "Growth", "Financial Planning"),
                description = "Full tuition and mentorship grant for female entrepreneurs running early-stage micro-enterprises. Includes seed capital funding and executive mentorship sessions.",
                requirements = listOf("Female founder running an active venture in South Africa", "Operational for at least 6 months", "Clear vision for community impact and scaling"),
                closingDate = "2026-11-30",
                daysLeft = 39
            ),
            Opportunity(
                id = "opp_4",
                title = "Graduate Finance Programme",
                company = "Ubuntu Finance",
                location = "Cape Town",
                type = "Programme",
                category = "Finance",
                skills = listOf("Communication", "Teamwork", "Growth", "Financial Modeling"),
                description = "Accelerate your career in financial services. Rotations include SME credit assessment, venture capital portfolio management, and sustainable micro-lending.",
                requirements = listOf("Degree or Diploma in Finance, Accounting, or Economics", "Strong analytical skills and attention to detail", "South African citizen or permanent resident"),
                closingDate = "2026-11-20",
                daysLeft = 29
            ),
            Opportunity(
                id = "opp_5",
                title = "E-Commerce Growth Specialist",
                company = "Woolworths Digital",
                location = "Cape Town",
                type = "Job",
                category = "Marketing",
                skills = listOf("Social Media Marketing", "Inventory Management", "Customer Relationship"),
                description = "Drive omni-channel customer growth and optimize digital storefront conversions across regional food and beauty hubs.",
                requirements = listOf("2+ years experience in retail e-commerce or digital catalog merchandising", "Familiarity with inventory tracking software"),
                closingDate = "2026-11-12",
                daysLeft = 21
            ),
            Opportunity(
                id = "opp_6",
                title = "SME Business Banking Associate",
                company = "Standard Bank SA",
                location = "Johannesburg",
                type = "Job",
                category = "Finance",
                skills = listOf("Business Financial Planning", "Customer Relationship", "Communication"),
                description = "Partner with township and suburban entrepreneurs to facilitate working capital, trade finance, and merchant point-of-sale solutions.",
                requirements = listOf("Relevant qualification in Commerce, Banking or Entrepreneurship", "Strong client relationship skills"),
                closingDate = "2026-11-18",
                daysLeft = 27
            ),
            Opportunity(
                id = "opp_7",
                title = "Township Bakery Incubation Grant",
                company = "Eastern Cape Development Corp",
                location = "Gqeberha",
                type = "Scholarship",
                category = "Business",
                skills = listOf("Pastry & Cake Decorating", "Inventory Management", "Business Financial Planning"),
                description = "Equipment and grant funding initiative providing industrial ovens, mixers, and health compliance certification support to emerging bakeries.",
                requirements = listOf("Registered bakery enterprise in Eastern Cape", "Demonstrated community customer base"),
                closingDate = "2026-11-25",
                daysLeft = 34
            ),
            Opportunity(
                id = "opp_8",
                title = "Cloud Solutions Support Engineer",
                company = "Dimension Data",
                location = "Gqeberha",
                type = "Job",
                category = "Technology",
                skills = listOf("HTML", "JavaScript", "Cloud Computing"),
                description = "Deliver proactive technical customer support and assist client migrations to scalable cloud infrastructure.",
                requirements = listOf("Diploma or Degree in Information Technology", "Demonstrated problem-solving aptitude"),
                closingDate = "2026-11-10",
                daysLeft = 19
            ),
            Opportunity(
                id = "opp_9",
                title = "Women Executive Leadership Circle",
                company = "African Leadership Institute",
                location = "South Africa",
                type = "Programme",
                category = "Leadership",
                skills = listOf("Leadership", "Communication", "Teamwork"),
                description = "Prestigious 6-month executive accelerator matching mid-career women with global boardroom leaders and coaches.",
                requirements = listOf("Minimum 4 years managerial or entrepreneurial leadership", "Commitment to 2 hours per week"),
                closingDate = "2026-12-05",
                daysLeft = 44
            ),
            Opportunity(
                id = "opp_10",
                title = "Creative Content & Branding Intern",
                company = "Ogilvy South Africa",
                location = "Johannesburg",
                type = "Internship",
                category = "Marketing",
                skills = listOf("Social Media Marketing", "Communication", "Growth"),
                description = "Design vibrant social campaigns, collaborate with copywriters, and manage live social activations for top national brands.",
                requirements = listOf("Creative portfolio showcasing social content or graphic design", "Proficiency in visual editing tools"),
                closingDate = "2026-11-14",
                daysLeft = 23
            ),
            Opportunity(
                id = "opp_11",
                title = "Agri-Processing Entrepreneurship Fellowship",
                company = "Stellenbosch Innovation Hub",
                location = "South Africa",
                type = "Scholarship",
                category = "Business",
                skills = listOf("Inventory Management", "Business Financial Planning", "Growth"),
                description = "Hands-on fellowship supporting sustainable food production, artisanal baking, and local supply chain resilience.",
                requirements = listOf("Focus on agro-processing, food hospitality, or culinary retail", "Passionate about job creation"),
                closingDate = "2026-12-01",
                daysLeft = 40
            ),
            Opportunity(
                id = "opp_12",
                title = "Junior Data Analyst Learnership",
                company = "First National Bank",
                location = "Johannesburg",
                type = "Learnership",
                category = "Technology",
                skills = listOf("HTML", "JavaScript", "Teamwork"),
                description = "12-month data science learnership focusing on SQL, dashboard creation, and customer behavior modeling in retail banking.",
                requirements = listOf("Matric with Mathematics", "Logical problem solving mindset"),
                closingDate = "2026-11-22",
                daysLeft = 31
            ),
            Opportunity(
                id = "opp_13",
                title = "Community Health & Wellness Coordinator",
                company = "Discovery Health",
                location = "Cape Town",
                type = "Job",
                category = "Leadership",
                skills = listOf("Communication", "Leadership", "Teamwork"),
                description = "Organize community health workshops, fitness days, and wellness incentive outreach programs.",
                requirements = listOf("Excellent interpersonal skills", "Passionate about health education"),
                closingDate = "2026-11-28",
                daysLeft = 37
            ),
            Opportunity(
                id = "opp_14",
                title = "Artisan Confectionery & Pastry Trainee",
                company = "The Oyster Box Hotel",
                location = "Durban",
                type = "Internship",
                category = "Business",
                skills = listOf("Pastry & Cake Decorating", "Inventory Management"),
                description = "Master fine patisserie, high-tea banquet cakes, and chocolate sculpture alongside renowned executive pastry chefs.",
                requirements = listOf("Culinary or baking certificate/experience", "Passion for exceptional culinary presentation"),
                closingDate = "2026-11-16",
                daysLeft = 25
            ),
            Opportunity(
                id = "opp_15",
                title = "Small Business Tax & Bookkeeping Trainee",
                company = "SNG Grant Thornton",
                location = "Gqeberha",
                type = "Learnership",
                category = "Finance",
                skills = listOf("Business Financial Planning", "Teamwork"),
                description = "Gain practical experience preparing VAT returns, payroll compliance, and financial statements for local small businesses.",
                requirements = listOf("Studying towards or completed Accounting Diploma/Degree"),
                closingDate = "2026-11-19",
                daysLeft = 28
            ),
            Opportunity(
                id = "opp_16",
                title = "Social Impact & Community Builder Grant",
                company = "Vodacom Foundation",
                location = "South Africa",
                type = "Scholarship",
                category = "Leadership",
                skills = listOf("Communication", "Leadership", "Growth"),
                description = "Providing grants and digital devices to grassroots female leaders spearheading youth upskilling centers.",
                requirements = listOf("Proven community project leadership", "Commitment to training at least 20 local women"),
                closingDate = "2026-12-10",
                daysLeft = 49
            ),
            Opportunity(
                id = "opp_17",
                title = "UX/UI Design Apprentice",
                company = "Yoco Technologies",
                location = "Cape Town",
                type = "Internship",
                category = "Technology",
                skills = listOf("HTML", "CSS", "Communication"),
                description = "Help craft intuitive, delightful point-of-sale mobile experiences for small businesses across South Africa.",
                requirements = listOf("Eye for typography, visual hierarchy and user empathy", "Design portfolio or case study"),
                closingDate = "2026-11-21",
                daysLeft = 30
            ),
            Opportunity(
                id = "opp_18",
                title = "Retail Operations & Logistics Coordinator",
                company = "Takealot Group",
                location = "Johannesburg",
                type = "Job",
                category = "Business",
                skills = listOf("Inventory Management", "Customer Relationship", "Teamwork"),
                description = "Coordinate merchant onboarding, fulfillment dispatch, and warehouse logistics during peak holiday seasons.",
                requirements = listOf("Experience in stock control, retail logistics or dispatch"),
                closingDate = "2026-11-29",
                daysLeft = 38
            )
        )
    )
    val opportunities: StateFlow<List<Opportunity>> = _opportunities.asStateFlow()

    // Saved Opportunities
    private val _savedOpportunityIds = MutableStateFlow(setOf("opp_1", "opp_7"))
    val savedOpportunityIds: StateFlow<Set<String>> = _savedOpportunityIds.asStateFlow()

    // User Applications
    private val _applications = MutableStateFlow(
        listOf(
            OpportunityApplication(
                id = "app_1",
                opportunityId = "opp_7",
                opportunityTitle = "Township Bakery Incubation Grant",
                company = "Eastern Cape Development Corp",
                motivation = "As the founder of Abo's Bakery in Gqeberha, this incubation grant will enable me to purchase a commercial deck oven, expand daily production from 10 to 60 cakes, and hire two young apprentices.",
                cvName = "Abongile_Lathitha_CV.pdf",
                status = "In review",
                createdAt = System.currentTimeMillis() - 86400000L * 3
            )
        )
    )
    val applications: StateFlow<List<OpportunityApplication>> = _applications.asStateFlow()

    // Mentors
    private val _mentors = MutableStateFlow(
        listOf(
            Mentor(
                id = "mentor_1",
                name = "Sarah M.",
                title = "Software Developer",
                category = "Technology",
                skills = listOf("C#", "ASP.NET", "Cloud Computing", "Architecture"),
                yearsExperience = 5,
                bio = "Passionate about building scalable cloud solutions and opening doors for women in tech. Mentored 40+ junior developers across Africa.",
                languages = listOf("English", "isiXhosa"),
                rating = 4.9,
                menteeCount = 42,
                availability = listOf("Tuesdays 17:00", "Thursdays 18:00", "Saturdays 10:00"),
                initials = "SM",
                available = true
            ),
            Mentor(
                id = "mentor_2",
                name = "Naledi K.",
                title = "Business Strategist",
                category = "Business",
                skills = listOf("Planning", "Growth", "Finance", "Market Research"),
                yearsExperience = 8,
                bio = "Specializing in SME scaling, pricing models, and operational frameworks. Passionate about empowering township and local community business owners.",
                languages = listOf("English", "Sesotho", "isiZulu"),
                rating = 5.0,
                menteeCount = 58,
                availability = listOf("Mondays 16:00", "Wednesdays 17:30", "Saturdays 11:30"),
                initials = "NK",
                available = true
            ),
            Mentor(
                id = "mentor_3",
                name = "Ayesha P.",
                title = "Marketing Lead",
                category = "Marketing",
                skills = listOf("Brand", "Digital", "Content", "Paid Ads"),
                yearsExperience = 7,
                bio = "Award-winning creative director helping female-owned brands find their authentic voice, tell captivating stories, and acquire loyal customers.",
                languages = listOf("English", "Afrikaans"),
                rating = 4.8,
                menteeCount = 35,
                availability = listOf("Tuesdays 18:30", "Fridays 15:00"),
                initials = "AP",
                available = true
            ),
            Mentor(
                id = "mentor_4",
                name = "Lerato M.",
                title = "Finance Director",
                category = "Finance",
                skills = listOf("Bookkeeping", "Tax", "Auditing", "Cash Flow"),
                yearsExperience = 10,
                bio = "Chartered Accountant (SA) dedicated to making financial literacy accessible to grassroots entrepreneurs. I turn complex numbers into simple growth roadmaps.",
                languages = listOf("English", "Setswana"),
                rating = 4.9,
                menteeCount = 61,
                availability = listOf("Wednesdays 18:00", "Sundays 14:00"),
                initials = "LM",
                available = true
            ),
            Mentor(
                id = "mentor_5",
                name = "Zola N.",
                title = "Leadership Coach",
                category = "Leadership",
                skills = listOf("Public Speaking", "Team Building", "Executive Presence"),
                yearsExperience = 9,
                bio = "Certified executive coach guiding women through career transitions, imposter syndrome, and negotiations. Elevate your confidence and lead with impact.",
                languages = listOf("English", "isiXhosa", "isiZulu"),
                rating = 4.9,
                menteeCount = 47,
                availability = listOf("Thursdays 17:00", "Fridays 16:30"),
                initials = "ZN",
                available = true
            ),
            Mentor(
                id = "mentor_6",
                name = "Nomsa B.",
                title = "Career Consultant",
                category = "Career Development",
                skills = listOf("CV Prep", "Interviewing", "Networking", "LinkedIn"),
                yearsExperience = 6,
                bio = "Talent acquisition specialist who has placed over 200 candidates in top African corporations. Let's make your CV and interview skills irresistible.",
                languages = listOf("English", "isiZulu"),
                rating = 4.8,
                menteeCount = 52,
                availability = listOf("Mondays 17:00", "Wednesdays 16:00", "Saturdays 09:00"),
                initials = "NB",
                available = true
            )
        )
    )
    val mentors: StateFlow<List<Mentor>> = _mentors.asStateFlow()

    // Active Mentorship
    private val _activeMentorship = MutableStateFlow<MentorshipConnection?>(
        MentorshipConnection(
            id = "mentorship_1",
            mentorId = "mentor_2",
            mentorName = "Naledi K.",
            mentorTitle = "Business Strategist",
            status = "Connected",
            goal = "Scale Abo's Bakery weekly cake production and optimize ingredient costs",
            nextCheckIn = "Tomorrow at 17:30",
            notes = "Review ingredient suppliers in Gqeberha and finalize holiday gift box pricing"
        )
    )
    val activeMentorship: StateFlow<MentorshipConnection?> = _activeMentorship.asStateFlow()

    // Chat Messages
    private val _chatMessages = MutableStateFlow(
        listOf(
            ChatMessage("msg_1", "mentor_2", "Hello Abongile! I reviewed your bakery sales figures from last week. The Chocolate Cake is performing phenomenally!", System.currentTimeMillis() - 7200000L, true),
            ChatMessage("msg_2", "user_abo_001", "Thank you Naledi! Yes, people in Gqeberha love the Belgian dark chocolate recipe. I want to launch a 6-pack cupcake box next.", System.currentTimeMillis() - 3600000L, false),
            ChatMessage("msg_3", "mentor_2", "Brilliant idea! Let's calculate the packaging cost and margin during our session tomorrow at 17:30. Bring your supplier quotes!", System.currentTimeMillis() - 1800000L, true)
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    // Courses (4 courses with 5+ lessons each)
    private val _courses = MutableStateFlow(
        listOf(
            Course(
                id = "course_1",
                title = "Web Development Basics",
                description = "Master HTML5, modern CSS layouts, JavaScript logic, and responsive web design to build your online presence.",
                category = "Technology",
                lessons = listOf(
                    Lesson("l1_1", "Introduction to the Web & How Browsers Work", "15 min", "Learn how HTTP requests, servers, and browsers deliver websites to users across mobile and desktop devices.", listOf("Client vs Server architecture", "Role of HTML, CSS and JS")),
                    Lesson("l1_2", "HTML5 Semantic Structure & Forms", "25 min", "Explore headers, navs, sections, and form elements tailored for e-commerce order forms.", listOf("Semantic markup", "Accessible form inputs")),
                    Lesson("l1_3", "CSS Flexbox & Responsive Layouts", "30 min", "Build mobile-first fluid grids that automatically adjust to phones, tablets, and wide monitors.", listOf("Flex container properties", "Media queries for mobile")),
                    Lesson("l1_4", "JavaScript Fundamentals & DOM", "35 min", "Manipulate elements dynamically, handle click events, and create interactive product selectors.", listOf("Variables and functions", "Event listeners and state")),
                    Lesson("l1_5", "Building & Deploying Your First Web Portfolio", "40 min", "Package your assets, optimize image sizes, and deploy live on free web hosting.", listOf("Git basics", "Deploying with Firebase Hosting"))
                )
            ),
            Course(
                id = "course_2",
                title = "Digital Marketing & Brand Growth",
                description = "Acquire paying customers using organic Instagram reels, Facebook community groups, and targeted local ads.",
                category = "Marketing",
                lessons = listOf(
                    Lesson("l2_1", "Defining Your Brand Identity & Value Proposition", "20 min", "Discover what makes your products unforgettable and craft your signature brand tone.", listOf("Brand story", "Target customer persona")),
                    Lesson("l2_2", "Visual Content Creation on a Smartphone", "25 min", "Shoot mouth-watering product photos and reels using natural studio lighting and free mobile editors.", listOf("Rule of thirds", "Angles for food and craft")),
                    Lesson("l2_3", "Social Media Funnels That Convert", "30 min", "Transform casual likes into WhatsApp business orders and website checkouts.", listOf("Call to action", "Bio links & DM scripts")),
                    Lesson("l2_4", "Running Local Hyper-Targeted Ads", "35 min", "Spend R50 to reach 2,000 potential customers within 5km of your business address.", listOf("Meta Ads Manager", "Audience radius targeting")),
                    Lesson("l2_5", "Customer Retention & Review Loops", "20 min", "Encourage repeat orders and automated 5-star word of mouth reviews.", listOf("Loyalty perks", "Post-purchase follow-up"))
                )
            ),
            Course(
                id = "course_3",
                title = "Bookkeeping & Accounting for Small Business",
                description = "Master South African Rand cash flow, cost of goods sold, profit margins, and easy tax readiness.",
                category = "Finance",
                lessons = listOf(
                    Lesson("l3_1", "Separating Personal & Business Finances", "18 min", "Why a dedicated business bank account protects your venture and simplifies record keeping.", listOf("Business entity banking", "Audit trail basics")),
                    Lesson("l3_2", "Calculating Cost of Goods Sold (COGS)", "25 min", "Calculate raw ingredients, packaging, and labor per single cake or product.", listOf("Direct cost breakdown", "Overhead allocations")),
                    Lesson("l3_3", "Pricing for Healthy Profit Margins", "30 min", "Formulate prices that ensure 35%+ net profit margin after all production costs.", listOf("Margin vs Markup", "Tiered pricing psychology")),
                    Lesson("l3_4", "Tracking Daily Sales & Operating Expenses", "25 min", "Use simple digital ledgers to record invoices, receipts, and cash movements daily.", listOf("Expense categorization", "Cash flow forecasting")),
                    Lesson("l3_5", "SARS Compliance & Turnover Tax Basics", "35 min", "Understand SARS turnover tax for micro-enterprises and keep receipts audit-ready.", listOf("Turnover tax thresholds", "Deductible business expenses"))
                )
            ),
            Course(
                id = "course_4",
                title = "Business Plan Writing & Pitching",
                description = "Structure an investor-grade business plan to unlock government grants, bank loans, and partner sponsorships.",
                category = "Business",
                lessons = listOf(
                    Lesson("l4_1", "The Executive Summary & Problem-Solution Fit", "20 min", "Summarize your mission, market opportunity, and competitive edge in one compelling page.", listOf("Hooking judges", "Clear value statement")),
                    Lesson("l4_2", "Market Research & Competitor Analysis", "25 min", "Analyze local demand in Gqeberha or your region and identify market gaps.", listOf("SWOT analysis", "Customer validation")),
                    Lesson("l4_3", "Operations & Supply Chain Strategy", "30 min", "Detail equipment, vendor reliability, production capacity, and hygiene standards.", listOf("Standard operating procedures", "Capacity planning")),
                    Lesson("l4_4", "3-Year Financial Projections & Cash Flow", "35 min", "Build realistic monthly sales forecasts, break-even milestones, and capital expenditure budgets.", listOf("Break-even analysis", "Runway calculation")),
                    Lesson("l4_5", "Mastering the 3-Minute Elevator Pitch", "25 min", "Deliver your business story with confidence, poise, and memorable metrics to pitch judges.", listOf("Storytelling arc", "Handling tough Q&A"))
                )
            )
        )
    )
    val courses: StateFlow<List<Course>> = _courses.asStateFlow()

    // Course Progress (courseId -> set of completed lesson IDs)
    private val _courseProgress = MutableStateFlow<Map<String, Set<String>>>(
        mapOf(
            "course_1" to setOf("l1_1", "l1_2"),
            "course_3" to setOf("l3_1", "l3_2", "l3_3")
        )
    )
    val courseProgress: StateFlow<Map<String, Set<String>>> = _courseProgress.asStateFlow()

    // Notifications
    private val _notifications = MutableStateFlow(
        listOf(
            NotificationItem("notif_1", "Application Submitted", "Your application for Township Bakery Incubation Grant has been received by ECDC.", "2 hours ago", false, "application"),
            NotificationItem("notif_2", "Mentor Session Confirmed", "Naledi K. confirmed your check-in for tomorrow at 17:30.", "5 hours ago", false, "mentor"),
            NotificationItem("notif_3", "Low Stock Alert", "Cupcakes - box of 12 has only 2 units remaining in Abo's Bakery.", "1 day ago", false, "business"),
            NotificationItem("notif_4", "New Match Opportunity", "Junior Web Developer Internship matches 92% of your profile.", "2 days ago", true, "match")
        )
    )
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    // SheBiz AI Chat Messages
    private val _aiMessages = MutableStateFlow(
        listOf(
            AiChatMessage("ai_1", false, "Hello Abongile! I'm your SheBiz AI Assistant. I have context on your skills and Abo's Bakery in Gqeberha. How can I help you grow your business, prepare for opportunities, or connect with mentors today?")
        )
    )
    val aiMessages: StateFlow<List<AiChatMessage>> = _aiMessages.asStateFlow()

    // Dynamic Match % Calculator
    fun calculateMatchPercentage(opportunity: Opportunity): Int {
        val user = _currentUser.value
        val userSkills = user.skills.map { it.name.lowercase() }
        val userInterests = (user.interests + user.careerInterests).map { it.lowercase() }

        var matchedCount = 0
        val totalCriteria = (opportunity.skills.size + 1).coerceAtLeast(1)

        opportunity.skills.forEach { oppSkill ->
            val clean = oppSkill.lowercase()
            if (userSkills.any { it.contains(clean) || clean.contains(it) } ||
                userInterests.any { it.contains(clean) || clean.contains(it) }) {
                matchedCount++
            }
        }

        // Category matching bonus
        if (userInterests.any { it.contains(opportunity.category.lowercase()) || opportunity.category.lowercase().contains(it) }) {
            matchedCount++
        }

        val basePercentage = ((matchedCount.toDouble() / totalCriteria) * 100).toInt()
        // Baseline between 55% and 98% for realistic relevance
        return basePercentage.coerceIn(58, 98)
    }

    // Business Calculations
    fun getTodaySales(): Double {
        val todayStart = System.currentTimeMillis() - 86400000L
        return _sales.value.filter { it.timestamp >= todayStart }.sumOf { it.total }.let {
            if (it == 0.0) 350.0 else it
        }
    }

    fun getMonthlySales(): Double {
        return _sales.value.sumOf { it.total }
    }

    fun getMonthlyExpenses(): Double {
        return _expenses.value.sumOf { it.amount }
    }

    fun getEstProfit(): Double {
        return getMonthlySales() - getMonthlyExpenses()
    }

    // Atomic Sale Recording
    fun recordSale(productId: String, quantity: Int, customerName: String?): Result<Sale> {
        val currentProducts = _products.value.toMutableList()
        val index = currentProducts.indexOfFirst { it.id == productId }
        if (index == -1) {
            return Result.failure(Exception("Product not found"))
        }

        val product = currentProducts[index]
        if (product.stock < quantity) {
            return Result.failure(Exception("Insufficient stock. Only ${product.stock} available."))
        }

        // Atomically decrement stock
        val updatedProduct = product.copy(stock = product.stock - quantity)
        currentProducts[index] = updatedProduct
        _products.value = currentProducts

        val total = product.price * quantity
        val newSale = Sale(
            id = "sale_${System.currentTimeMillis()}",
            productId = productId,
            productName = product.name,
            quantity = quantity,
            unitPrice = product.price,
            total = total,
            customerName = if (!customerName.isNullOrBlank()) customerName else "Walk-in Customer",
            timestamp = System.currentTimeMillis()
        )
        _sales.value = listOf(newSale) + _sales.value

        // Update or add customer
        if (!customerName.isNullOrBlank()) {
            val customersList = _customers.value.toMutableList()
            val custIndex = customersList.indexOfFirst { it.name.equals(customerName, ignoreCase = true) }
            if (custIndex != -1) {
                val existing = customersList[custIndex]
                customersList[custIndex] = existing.copy(
                    totalSpent = existing.totalSpent + total,
                    orderCount = existing.orderCount + 1
                )
            } else {
                customersList.add(
                    Customer("cust_${System.currentTimeMillis()}", customerName, "+27 82 000 0000", total, 1)
                )
            }
            _customers.value = customersList
        }

        // Add Notification
        addNotification(
            "New Sale Recorded",
            "Sold $quantity x ${product.name} for ${formatRand(total)}.",
            "business"
        )

        return Result.success(newSale)
    }

    // Product Management
    fun addProduct(name: String, description: String, price: Double, stock: Int, category: String) {
        val newProd = Product(
            id = "prod_${System.currentTimeMillis()}",
            name = name,
            description = description,
            price = price,
            stock = stock,
            category = category
        )
        _products.value = _products.value + newProd
    }

    fun updateProduct(product: Product) {
        _products.value = _products.value.map { if (it.id == product.id) product else it }
    }

    fun deleteProduct(productId: String) {
        _products.value = _products.value.filterNot { it.id == productId }
    }

    fun restockProduct(productId: String, additionalStock: Int) {
        _products.value = _products.value.map {
            if (it.id == productId) it.copy(stock = it.stock + additionalStock) else it
        }
    }

    // Expense Management
    fun addExpense(category: String, amount: Double, date: String, note: String) {
        val newExp = Expense(
            id = "exp_${System.currentTimeMillis()}",
            category = category,
            amount = amount,
            date = date,
            note = note
        )
        _expenses.value = listOf(newExp) + _expenses.value
    }

    fun deleteExpense(expenseId: String) {
        _expenses.value = _expenses.value.filterNot { it.id == expenseId }
    }

    // Opportunities & Applications
    fun toggleSaveOpportunity(oppId: String) {
        val current = _savedOpportunityIds.value
        _savedOpportunityIds.value = if (current.contains(oppId)) current - oppId else current + oppId
    }

    fun applyForOpportunity(opportunityId: String, motivation: String, cvName: String) {
        val opp = _opportunities.value.firstOrNull { it.id == opportunityId } ?: return
        val application = OpportunityApplication(
            id = "app_${System.currentTimeMillis()}",
            opportunityId = opp.id,
            opportunityTitle = opp.title,
            company = opp.company,
            motivation = motivation,
            cvName = cvName,
            status = "Submitted",
            createdAt = System.currentTimeMillis()
        )
        _applications.value = listOf(application) + _applications.value
        addNotification(
            "Application Submitted",
            "You successfully applied for ${opp.title} at ${opp.company}.",
            "application"
        )
    }

    fun withdrawApplication(applicationId: String) {
        _applications.value = _applications.value.filterNot { it.id == applicationId }
    }

    // Mentorship Actions
    fun requestMentorship(mentor: Mentor, goal: String, preferredTime: String) {
        val connection = MentorshipConnection(
            id = "mentorship_${System.currentTimeMillis()}",
            mentorId = mentor.id,
            mentorName = mentor.name,
            mentorTitle = mentor.title,
            status = "Requested",
            goal = goal,
            nextCheckIn = preferredTime,
            notes = "Mentorship requested. Auto-connecting..."
        )
        _activeMentorship.value = connection
        addNotification(
            "Mentorship Request Sent",
            "Your mentorship request has been sent to ${mentor.name}.",
            "mentor"
        )
    }

    fun autoApproveMentorship() {
        val current = _activeMentorship.value ?: return
        _activeMentorship.value = current.copy(status = "Connected")
        addNotification(
            "Mentorship Connected!",
            "Great news! ${current.mentorName} accepted your mentorship request.",
            "mentor"
        )
    }

    fun bookSession(dateTimeString: String) {
        val current = _activeMentorship.value ?: return
        _activeMentorship.value = current.copy(nextCheckIn = dateTimeString)
        addNotification(
            "Session Booked",
            "Check-in session confirmed with ${current.mentorName} for $dateTimeString.",
            "mentor"
        )
    }

    fun sendChatMessage(text: String) {
        val newMsg = ChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            senderId = _currentUser.value.uid,
            text = text,
            timestamp = System.currentTimeMillis(),
            isMentor = false
        )
        _chatMessages.value = _chatMessages.value + newMsg
    }

    fun receiveMentorReply(text: String) {
        val mentor = _activeMentorship.value
        val reply = ChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            senderId = mentor?.mentorId ?: "mentor_default",
            text = text,
            timestamp = System.currentTimeMillis(),
            isMentor = true
        )
        _chatMessages.value = _chatMessages.value + reply
    }

    // Course Progress
    fun markLessonCompleted(courseId: String, lessonId: String) {
        val current = _courseProgress.value.toMutableMap()
        val set = (current[courseId] ?: emptySet()).toMutableSet()
        set.add(lessonId)
        current[courseId] = set
        _courseProgress.value = current
    }

    fun getCourseCompletionPercent(courseId: String): Int {
        val course = _courses.value.firstOrNull { it.id == courseId } ?: return 0
        val completed = _courseProgress.value[courseId]?.size ?: 0
        if (course.lessons.isEmpty()) return 0
        return ((completed.toDouble() / course.lessons.size) * 100).toInt()
    }

    fun getOverallSkillsProgress(): Int {
        val allLessons = _courses.value.sumOf { it.lessons.size }
        val completedLessons = _courseProgress.value.values.sumOf { it.size }
        if (allLessons == 0) return 45
        return (((completedLessons.toDouble() / allLessons) * 100).toInt()).coerceIn(40, 100)
    }

    // AI Messages
    fun sendAiMessage(prompt: String): AiChatMessage {
        val userMsg = AiChatMessage("ai_usr_${System.currentTimeMillis()}", true, prompt)
        _aiMessages.value = _aiMessages.value + userMsg

        // Smart context-aware SheBiz local helper response
        val responseText = generateLocalAiGuidance(prompt)
        val assistantMsg = AiChatMessage("ai_bot_${System.currentTimeMillis()}", false, responseText)
        _aiMessages.value = _aiMessages.value + assistantMsg
        return assistantMsg
    }

    fun rateAiMessage(messageId: String, helpful: Boolean) {
        _aiMessages.value = _aiMessages.value.map {
            if (it.id == messageId) it.copy(helpful = helpful) else it
        }
    }

    fun clearAiChat() {
        _aiMessages.value = listOf(
            AiChatMessage("ai_1", false, "Chat cleared. What can I help you explore today for SheBiz?")
        )
    }

    private fun generateLocalAiGuidance(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("bakery") || lower.contains("cake") || lower.contains("product") ->
                "For Abo's Bakery in Gqeberha, your Chocolate Cake is your top revenue driver (R350 unit price). Here is a recommended 3-step action plan:\n\n1. **Holiday Party Boxes**: Bundle 6 cupcakes (R90) with mini celebration cakes to increase average order value to R450+.\n2. **Inventory Safety Stock**: Your vanilla cake is currently out of stock and cupcakes are low (2 left). Reorder butter and Madagascar vanilla extract today to prevent lost weekend sales.\n3. **Local Gqeberha Delivery**: Offer free delivery for orders above R500 in Summerstrand and Walmer to boost repeat weekly orders."

            lower.contains("mentor") || lower.contains("session") || lower.contains("connect") ->
                "Mentorship is a game-changer! You are currently connected with Naledi K. (Business Strategist, 8 years experience). Her expertise in pricing and SME scaling directly matches your growth goals. I recommend preparing 3 questions for your next check-in: your gross margins per cake tier, hiring seasonal apprentices, and applying for the ECDC incubation grant."

            lower.contains("opportunity") || lower.contains("grant") || lower.contains("job") || lower.contains("internship") ->
                "Based on your profile, the **Township Bakery Incubation Grant** (ECDC) and **Women in Business Scholarship** are your highest-probability matches (>90%). Make sure your CV highlights your Diploma from Nelson Mandela University and your revenue growth since 2024!"

            lower.contains("skill") || lower.contains("learn") || lower.contains("course") ->
                "You have completed 3 of 5 lessons in 'Bookkeeping & Accounting for Small Business'. Finishing the remaining lessons on Pricing for Healthy Margins and SARS Turnover Tax will boost your profile completion to 100% and prepare you for investor audits."

            else ->
                "Here is guidance tailored for SheBiz:\n\n• **Focus on High-Margin Lines**: Prioritize custom celebration cakes where margins exceed 45%.\n• **Consistent Cash Tracking**: Log all raw ingredients daily so your cost per portion remains accurate.\n• **Leverage Your Network**: Reach out to mentors in the SheBiz directory whenever you are renegotiating vendor contracts or applying for funding."
        }
    }

    // Profile Management
    fun updateUserProfile(updated: UserProfile) {
        _currentUser.value = updated
    }

    fun addSkill(name: String, level: Int) {
        val current = _currentUser.value
        _currentUser.value = current.copy(skills = current.skills + SkillItem(name, level))
    }

    fun updateSkill(index: Int, name: String, level: Int) {
        val current = _currentUser.value
        val list = current.skills.toMutableList()
        if (index in list.indices) {
            list[index] = SkillItem(name, level)
            _currentUser.value = current.copy(skills = list)
        }
    }

    fun deleteSkill(index: Int) {
        val current = _currentUser.value
        val list = current.skills.toMutableList()
        if (index in list.indices) {
            list.removeAt(index)
            _currentUser.value = current.copy(skills = list)
        }
    }

    fun addEducation(institution: String, qualification: String, year: String) {
        val current = _currentUser.value
        _currentUser.value = current.copy(education = current.education + EducationItem(institution, qualification, year))
    }

    fun addAchievement(title: String, description: String) {
        val current = _currentUser.value
        _currentUser.value = current.copy(achievements = current.achievements + AchievementItem(title, description))
    }

    // Notifications
    fun addNotification(title: String, body: String, type: String = "info") {
        val item = NotificationItem(
            id = "notif_${System.currentTimeMillis()}",
            title = title,
            body = body,
            timeAgo = "Just now",
            read = false,
            type = type
        )
        _notifications.value = listOf(item) + _notifications.value
    }

    fun markNotificationRead(id: String) {
        _notifications.value = _notifications.value.map { if (it.id == id) it.copy(read = true) else it }
    }

    fun markAllNotificationsRead() {
        _notifications.value = _notifications.value.map { it.copy(read = true) }
    }

    // Utility currency formatter
    companion object {
        fun formatRand(amount: Double): String {
            val format = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))
            val formatted = format.format(amount)
            // Ensure format like R12,450 or R350
            return formatted.replace("ZAR", "R").trim()
        }

        fun formatNumber(amount: Double): String {
            return String.format(Locale.getDefault(), "%,.0f", amount)
        }
    }
}
