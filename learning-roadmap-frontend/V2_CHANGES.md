# 📘 Frontend V2 Changes (Tenglish)

Backend lo **JWT login** add chesam, anduke frontend lo kuda changes chesam. Ikkada em marindi simple ga undi.

## Em marindi?

| File | Change |
|---|---|
| `src/services/api.js` | Token ni automatic ga `Authorization: Bearer <token>` header lo pampistundi. 401 vaste logout chestundi. `registerUser`, `loginUser`, `getErrorMessage` add. `getUserHistory()` ki email avasaram ledu |
| `src/context/AuthContext.js` 🆕 | "Evaru logged in" ani app antha share chestundi (`useAuth()`). Token `localStorage` lo save avutundi, refresh chesina login alage untundi |
| `src/components/ProtectedRoute.js` 🆕 | Login lekapote `/login` ki pampistundi. Login tarvata malli aa page ki techchestundi |
| `src/pages/LoginPage.js`, `RegisterPage.js`, `AuthPage.css` 🆕 | Login / Sign up pages |
| `src/App.js` | `AuthProvider` + protected routes (`/generate`, `/result`, `/history`) |
| `src/components/Navbar.js` | Login lekapote: Login + Sign Up. Login aite: Generate, History, "Hi, name", Logout |
| `src/pages/GeneratePage.js` | Name/email step teeseyam (token nundi vastundi). 3 steps → **2 steps** (Role, Skills). Server error messages chupistundi |
| `src/pages/HistoryPage.js` | Email search teeseyam. Page open aite **mee roadmaps** automatic ga vastayi. `currentSkills` ippudu array |
| `src/pages/HomePage.js` | "No account needed" text marchamu |
| `.gitignore` | Mundu empty. Ippudu `node_modules/`, `build/`, `.env` |
| `.env.example` 🆕 | Backend URL marchalante (`REACT_APP_API_URL`) |

## Flow
1. User `/register` leka `/login` lo details istadu → backend token istundi
2. Token `localStorage` lo save avutundi (`AuthContext`)
3. Prathi API call ki `api.js` token ni header lo pedtundi
4. Token expire ayite backend `401` istundi → app automatic ga logout chesi `/login` ki pampistundi

## Run cheyadam
```bash
npm install
npm start        # http://localhost:3000
```
Backend `.env` lo `CORS_ALLOWED_ORIGINS` lo mee frontend URL undali (default `http://localhost:3000`). Mee React port vere aite (e.g. 3036) adi ikkada pettandi.

> 💡 `localStorage` lo token pettadam learning project ki OK. Production lo httpOnly cookies / refresh tokens vaadatharu.
