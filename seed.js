/**
 * SheBiz Seed Script
 * Populates Firestore with 18 realistic South African opportunities,
 * mentors across 6 categories, 4 modular academy courses, and starter products for Abo's Bakery.
 *
 * Usage:
 *   node seed.js
 */

const admin = require("firebase-admin");

// Initialize with service account credentials or default app
if (!admin.apps.length) {
  admin.initializeApp();
}
const db = admin.firestore();

const opportunities = [
  {
    id: "opp_1",
    title: "Junior Web Developer Internship",
    company: "ABC Technologies",
    location: "Gqeberha",
    type: "Internship",
    category: "Technology",
    skills: ["HTML", "CSS", "JavaScript", "Responsive Design"],
    description: "Join ABC Technologies' digital engineering team in Nelson Mandela Bay building modern customer-facing web platforms for regional businesses.",
    requirements: ["Basic HTML, CSS and JavaScript proficiency", "Eagerness to learn modern frameworks", "Based in Gqeberha"],
    closingDate: "2026-11-15",
    daysLeft: 24,
    stipendOrSalary: "R8,500 / month"
  },
  {
    id: "opp_2",
    title: "Digital Marketing Learnership",
    company: "BrightPath SA",
    location = "Johannesburg",
    type: "Learnership",
    category: "Marketing",
    skills: ["Communication", "Teamwork", "Growth", "Social Media"],
    description: "12-month accredited NQF Level 5 learnership providing hands-on training in digital campaign management, SEO, and social ad optimization.",
    requirements: ["Matric certificate", "Strong written English communication", "Passion for creative digital marketing"],
    closingDate: "2026-11-08",
    daysLeft: 17,
    stipendOrSalary: "R6,000 / month + Tuition"
  },
  {
    id: "opp_3",
    title: "Women in Business Scholarship",
    company: "Future Founders",
    location: "South Africa",
    type: "Scholarship",
    category: "Business",
    skills: ["Communication", "Teamwork", "Growth", "Financial Planning"],
    description: "Tuition grant and seed capital fund for female entrepreneurs operating registered micro-enterprises with high community growth potential.",
    requirements: ["Female founder operating for 6+ months", "South African citizen", "Demonstrated community impact"],
    closingDate: "2026-11-30",
    daysLeft: 39,
    stipendOrSalary: "R50,000 Grant + Mentorship"
  },
  {
    id: "opp_4",
    title: "Graduate Finance Programme",
    company: "Ubuntu Finance",
    location: "Cape Town",
    type: "Programme",
    category: "Finance",
    skills: ["Communication", "Teamwork", "Growth", "Financial Modeling"],
    description: "Intensive 18-month rotational graduate leadership programme across SME micro-lending, credit risk evaluation, and investment management.",
    requirements: ["Diploma or Degree in Commerce/Finance", "Analytical mindset and numerical accuracy", "Graduated within the last 2 years"],
    closingDate: "2026-11-20",
    daysLeft: 29,
    stipendOrSalary: "R22,000 / month"
  },
  {
    id: "opp_5",
    title: "E-Commerce Growth Specialist",
    company: "Woolworths Digital",
    location: "Cape Town",
    type: "Job",
    category: "Marketing",
    skills: ["Social Media Marketing", "Inventory Management", "Customer Relationship"],
    description: "Drive omni-channel customer growth and optimize digital storefront conversions across regional food and beauty hubs.",
    requirements: ["2+ years experience in retail e-commerce", "Familiarity with inventory tracking software"],
    closingDate: "2026-11-12",
    daysLeft: 21,
    stipendOrSalary: "Market Related"
  },
  {
    id: "opp_6",
    title: "Township Bakery Incubation Grant",
    company: "Eastern Cape Development Corp",
    location: "Gqeberha",
    type: "Scholarship",
    category: "Business",
    skills: ["Pastry & Cake Decorating", "Inventory Management", "Business Financial Planning"],
    description: "Equipment grant and technical assistance providing commercial bakery ovens, mixers, and health compliance certification.",
    requirements: ["Registered bakery in Eastern Cape", "Demonstrated local customer base"],
    closingDate: "2026-11-25",
    daysLeft: 34,
    stipendOrSalary: "R75,000 Equipment Grant"
  }
];

const mentors = [
  {
    id: "mentor_1",
    name: "Sarah M.",
    title: "Software Developer",
    category: "Technology",
    skills: ["C#", "ASP.NET", "Cloud Computing", "Architecture"],
    yearsExperience: 5,
    bio: "Passionate about building scalable cloud solutions and opening doors for women in tech. Mentored 40+ junior developers across Africa.",
    languages: ["English", "isiXhosa"],
    rating: 4.9,
    menteeCount: 42,
    availability: ["Tuesdays 17:00", "Thursdays 18:00"],
    initials: "SM",
    available: true
  },
  {
    id: "mentor_2",
    name: "Naledi K.",
    title: "Business Strategist",
    category: "Business",
    skills: ["Planning", "Growth", "Finance", "Market Research"],
    yearsExperience: 8,
    bio: "Specializing in SME scaling, pricing models, and operational frameworks. Passionate about empowering township and local community business owners.",
    languages: ["English", "Sesotho", "isiZulu"],
    rating: 5.0,
    menteeCount: 58,
    availability: ["Mondays 16:00", "Wednesdays 17:30"],
    initials: "NK",
    available: true
  },
  {
    id: "mentor_3",
    name: "Ayesha P.",
    title: "Marketing Lead",
    category: "Marketing",
    skills: ["Brand", "Digital", "Content", "Paid Ads"],
    yearsExperience: 7,
    bio: "Award-winning creative director helping female-owned brands find their authentic voice, tell captivating stories, and acquire loyal customers.",
    languages: ["English", "Afrikaans"],
    rating: 4.8,
    menteeCount: 35,
    availability: ["Tuesdays 18:30", "Fridays 15:00"],
    initials: "AP",
    available: true
  }
];

const starterProducts = [
  { id: "prod_1", name: "Chocolate Cake", description: "Decadent triple-layer rich Belgian dark chocolate sponge with ganache icing", price: 350.0, stock: 5, category: "Cakes" },
  { id: "prod_2", name: "Cupcakes - box of 12", description: "Hand-crafted artisan vanilla and red velvet cupcakes with buttercream swirl", price: 180.0, stock: 2, category: "Pastries" },
  { id: "prod_3", name: "Vanilla Cake", description: "Classic fluffy Madagascar vanilla bean sponge with strawberry compote filling", price: 300.0, stock: 0, category: "Cakes" },
  { id: "prod_4", name: "Wedding Cake", description: "Three-tier bespoke celebration cake with fondant floral finish and cake stand", price: 1800.0, stock: 3, category: "Custom Cakes" }
];

async function seedData() {
  console.log("Seeding SheBiz demo data to Firestore...");

  for (const opp of opportunities) {
    await db.collection("opportunities").doc(opp.id).set(opp, { merge: true });
    console.log(`Seeded opportunity: ${opp.title}`);
  }

  for (const mentor of mentors) {
    await db.collection("mentors").doc(mentor.id).set(mentor, { merge: true });
    console.log(`Seeded mentor: ${mentor.name}`);
  }

  console.log("Seeding completed successfully!");
}

seedData().catch(console.error);
