import { lazy, Suspense } from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './routes/ProtectedRoute';
import AiChatWidget from './components/ai/AiChatWidget';

const HomePage = lazy(() => import('./pages/public/HomePage'));
const PackagesPage = lazy(() => import('./pages/public/PackagesPage'));
const BlogListPage = lazy(() => import('./pages/public/BlogListPage'));
const BlogDetailPage = lazy(() => import('./pages/public/BlogDetailPage'));
const AboutPage = lazy(() => import('./pages/public/AboutPage'));
const PtListPage = lazy(() => import('./pages/public/PtListPage'));
const LoginPage = lazy(() => import('./pages/auth/LoginPage'));
const RegisterPage = lazy(() => import('./pages/auth/RegisterPage'));
const ForgotPasswordPage = lazy(() => import('./pages/auth/ForgotPasswordPage'));
const MemberDashboard = lazy(() => import('./pages/member/MemberDashboard'));
const BuyPackagePage = lazy(() => import('./pages/member/BuyPackagePage'));
const MemberTransactions = lazy(() => import('./pages/member/MemberTransactions'));
const MembershipManagePage = lazy(() => import('./pages/member/MembershipManagePage'));
const MemberSchedulePage = lazy(() => import('./pages/member/MemberSchedulePage'));
const MemberDietPage = lazy(() => import('./pages/member/MemberDietPage'));
const PtDashboard = lazy(() => import('./pages/pt/PtDashboard'));
const PtMembersList = lazy(() => import('./pages/pt/PtMembersList'));
const PtMemberDetail = lazy(() => import('./pages/pt/PtMemberDetail'));
const PtProfilePage = lazy(() => import('./pages/pt/PtProfilePage'));
const PtReviewsPage = lazy(() => import('./pages/pt/PtReviewsPage'));
const PtSchedulePage = lazy(() => import('./pages/pt/PtSchedulePage'));
const AdminDashboard = lazy(() => import('./pages/admin/AdminDashboard'));
const TransactionsManagement = lazy(() => import('./pages/admin/TransactionsManagement'));
const PackagesManagement = lazy(() => import('./pages/admin/PackagesManagement'));
const UsersManagement = lazy(() => import('./pages/admin/UsersManagement'));
const PromotionsManagement = lazy(() => import('./pages/admin/PromotionsManagement'));
const BlogsManagement = lazy(() => import('./pages/admin/BlogsManagement'));
const ExercisesManagement = lazy(() => import('./pages/admin/ExercisesManagement'));
const DiscountsManagement = lazy(() => import('./pages/admin/DiscountsManagement'));
const ProfilePage = lazy(() => import('./pages/profile/ProfilePage'));

import './index.css';

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Suspense fallback={<div className="page-loading">Đang tải...</div>}>
          <Routes>
          {/* === Trang công khai === */}
          <Route path="/" element={<HomePage />} />
          <Route path="/packages" element={<PackagesPage />} />
          <Route path="/services" element={<PackagesPage />} />
          <Route path="/blog" element={<BlogListPage />} />
          <Route path="/blog/:id" element={<BlogDetailPage />} />
          <Route path="/about" element={<AboutPage />} />
          <Route path="/pts" element={<PtListPage />} />

          {/* === Auth === */}
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/forgot-password" element={<ForgotPasswordPage />} />

          {/* === Profile Chung (Ai đăng nhập cũng vào được) === */}
          <Route path="/profile" element={
            <ProtectedRoute>
              <ProfilePage />
            </ProtectedRoute>
          } />

          {/* === Member (cần đăng nhập + role MEMBER) === */}
          <Route path="/member/dashboard" element={
            <ProtectedRoute allowedRoles={['MEMBER']}>
              <MemberDashboard />
            </ProtectedRoute>
          } />
          <Route path="/member/buy-package" element={
            <ProtectedRoute allowedRoles={['MEMBER']}>
              <BuyPackagePage />
            </ProtectedRoute>
          } />
          <Route path="/member/transactions" element={
            <ProtectedRoute allowedRoles={['MEMBER']}>
              <MemberTransactions />
            </ProtectedRoute>
          } />
          <Route path="/member/membership" element={
            <ProtectedRoute allowedRoles={['MEMBER']}>
              <MembershipManagePage />
            </ProtectedRoute>
          } />
          <Route path="/member/schedule" element={
            <ProtectedRoute allowedRoles={['MEMBER']}>
              <MemberSchedulePage />
            </ProtectedRoute>
          } />
          <Route path="/member/diet" element={
            <ProtectedRoute allowedRoles={['MEMBER']}>
              <MemberDietPage />
            </ProtectedRoute>
          } />

          {/* === PT (cần đăng nhập + role PT) === */}
          <Route path="/pt/dashboard" element={
            <ProtectedRoute allowedRoles={['PT']}>
              <PtDashboard />
            </ProtectedRoute>
          } />
          <Route path="/pt/schedule" element={
            <ProtectedRoute allowedRoles={['PT']}>
              <PtSchedulePage />
            </ProtectedRoute>
          } />
          <Route path="/pt/members" element={
            <ProtectedRoute allowedRoles={['PT']}>
              <PtMembersList />
            </ProtectedRoute>
          } />
          <Route path="/pt/members/:memberId" element={
            <ProtectedRoute allowedRoles={['PT']}>
              <PtMemberDetail />
            </ProtectedRoute>
          } />
          <Route path="/pt/profile" element={
            <ProtectedRoute allowedRoles={['PT']}>
              <PtProfilePage />
            </ProtectedRoute>
          } />
          <Route path="/pt/reviews" element={
            <ProtectedRoute allowedRoles={['PT']}>
              <PtReviewsPage />
            </ProtectedRoute>
          } />

          {/* === Admin (cần đăng nhập + role ADMIN) === */}
          <Route path="/admin" element={
            <ProtectedRoute allowedRoles={['ADMIN']}>
              <AdminDashboard />
            </ProtectedRoute>
          } />
          <Route path="/admin/transactions" element={
            <ProtectedRoute allowedRoles={['ADMIN']}>
              <TransactionsManagement />
            </ProtectedRoute>
          } />
          <Route path="/admin/packages" element={
            <ProtectedRoute allowedRoles={['ADMIN']}>
              <PackagesManagement />
            </ProtectedRoute>
          } />
          <Route path="/admin/users" element={
            <ProtectedRoute allowedRoles={['ADMIN']}>
              <UsersManagement />
            </ProtectedRoute>
          } />
          <Route path="/admin/promotions" element={
            <ProtectedRoute allowedRoles={['ADMIN']}>
              <PromotionsManagement />
            </ProtectedRoute>
          } />
          <Route path="/admin/blogs" element={
            <ProtectedRoute allowedRoles={['ADMIN']}>
              <BlogsManagement />
            </ProtectedRoute>
          } />
          <Route path="/admin/exercises" element={
            <ProtectedRoute allowedRoles={['ADMIN']}>
              <ExercisesManagement />
            </ProtectedRoute>
          } />
          <Route path="/admin/discounts" element={
            <ProtectedRoute allowedRoles={['ADMIN']}>
              <DiscountsManagement />
            </ProtectedRoute>
          } />
          </Routes>
          <AiChatWidget />
        </Suspense>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
