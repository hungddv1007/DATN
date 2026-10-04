import React from 'react';
import MainLayout from '../../components/layout/MainLayout';
import HeroSection from '../../components/home/HeroSection';
import AboutSection from '../../components/home/AboutSection';
import PackagesSection from '../../components/home/PackagesSection';
import PtSection from '../../components/home/PtSection';
import NewsVideoSection from '../../components/home/NewsVideoSection';
import CustomerReviewsSection from '../../components/home/CustomerReviewsSection';

const HomePage = () => {
  return (
    <MainLayout>
      <HeroSection />
      <AboutSection />
      <PackagesSection />
      <PtSection />
      <CustomerReviewsSection />
      <NewsVideoSection />
    </MainLayout>
  );
};

export default HomePage;
