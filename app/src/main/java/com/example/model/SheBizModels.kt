package com.example.model

data class UserProfile(
    val uid: String = "user_abo_001",
    val fullName: String = "Abongile Lathitha",
    val email: String = "abongilelathitha@gmail.com",
    val phone: String = "+27 82 555 1234",
    val location: String = "Gqeberha, Eastern Cape",
    val interests: List<String> = listOf("Growing my business", "Finding a mentor", "Learning new skills"),
    val careerInterests: List<String> = listOf("Entrepreneurship", "Baking & Culinary Arts", "Digital Marketing"),
    val photoUrl: String? = null,
    val education: List<EducationItem> = listOf(
        EducationItem("Nelson Mandela University", "Diploma in Business Management", "2022")
    ),
    val achievements: List<AchievementItem> = listOf(
        AchievementItem("Eastern Cape Youth Entrepreneur Finalist", "Recognized for innovative community bakery operations in 2024")
    ),
    val skills: List<SkillItem> = listOf(
        SkillItem("Pastry & Cake Decorating", 90),
        SkillItem("Business Financial Planning", 72),
        SkillItem("Customer Relationship", 85),
        SkillItem("Social Media Marketing", 65),
        SkillItem("Inventory Management", 78)
    ),
    val emailVerified: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) {
    val profileComplete: Int
        get() {
            var score = 0
            if (fullName.isNotBlank()) score += 15
            if (email.isNotBlank()) score += 15
            if (phone.isNotBlank()) score += 15
            if (location.isNotBlank()) score += 10
            if (interests.isNotEmpty()) score += 15
            if (skills.isNotEmpty()) score += 15
            if (education.isNotEmpty()) score += 15
            return score.coerceAtMost(100)
        }
}

data class SkillItem(
    val name: String,
    val level: Int // 0 - 100
)

data class EducationItem(
    val institution: String,
    val qualification: String,
    val year: String
)

data class AchievementItem(
    val title: String,
    val description: String
)

data class Opportunity(
    val id: String,
    val title: String,
    val company: String,
    val location: String,
    val type: String, // Job, Internship, Learnership, Scholarship, Programme
    val category: String, // Technology, Marketing, Business, Finance, Leadership
    val skills: List<String>,
    val description: String,
    val requirements: List<String>,
    val closingDate: String,
    val daysLeft: Int,
    val stipendOrSalary: String = "Competitive / Funded"
)

data class OpportunityApplication(
    val id: String,
    val opportunityId: String,
    val opportunityTitle: String,
    val company: String,
    val motivation: String,
    val cvName: String,
    val status: String, // Submitted, In review, Shortlisted, Declined
    val createdAt: Long = System.currentTimeMillis()
)

data class Mentor(
    val id: String,
    val name: String,
    val title: String,
    val category: String, // Technology, Business, Finance, Marketing, Leadership, Career Development
    val skills: List<String>,
    val yearsExperience: Int,
    val bio: String,
    val languages: List<String>,
    val rating: Double,
    val menteeCount: Int,
    val availability: List<String>,
    val initials: String,
    val available: Boolean = true
)

data class MentorshipConnection(
    val id: String,
    val mentorId: String,
    val mentorName: String,
    val mentorTitle: String,
    val status: String, // Requested, Connected, Completed
    val goal: String,
    val nextCheckIn: String,
    val notes: String = ""
)

data class ChatMessage(
    val id: String,
    val senderId: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isMentor: Boolean = false
)

data class BusinessProfile(
    val name: String = "Abo's Bakery",
    val location: String = "Gqeberha",
    val setupProgress: Int = 85
)

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val category: String = "Bakery"
) {
    // 0 = Out of stock (red), 1-2 = Low stock (gold), 3+ = Available (green)
    val stockStatus: StockStatus
        get() = when {
            stock <= 0 -> StockStatus.OUT_OF_STOCK
            stock in 1..2 -> StockStatus.LOW_STOCK
            else -> StockStatus.AVAILABLE
        }
}

enum class StockStatus {
    AVAILABLE,
    LOW_STOCK,
    OUT_OF_STOCK
}

data class Sale(
    val id: String,
    val productId: String,
    val productName: String,
    val quantity: Int,
    val unitPrice: Double,
    val total: Double,
    val customerName: String = "Walk-in Customer",
    val timestamp: Long = System.currentTimeMillis()
)

data class Expense(
    val id: String,
    val category: String, // Ingredients, Packaging, Transport, Utilities, Marketing, Other
    val amount: Double,
    val date: String,
    val note: String
)

data class Customer(
    val id: String,
    val name: String,
    val phone: String,
    val totalSpent: Double,
    val orderCount: Int
)

data class Course(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val lessons: List<Lesson>
)

data class Lesson(
    val id: String,
    val title: String,
    val duration: String,
    val content: String,
    val keyTakeaways: List<String>
)

data class NotificationItem(
    val id: String,
    val title: String,
    val body: String,
    val timeAgo: String,
    val read: Boolean = false,
    val type: String = "info"
)

data class AiChatMessage(
    val id: String,
    val isUser: Boolean,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val helpful: Boolean? = null
)
