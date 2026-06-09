import React from 'react';
import { Link } from 'react-router-dom';
import { Clock, User } from 'lucide-react';
import MainLayout from '../../components/layout/MainLayout';
import './BlogPage.css';

// Dữ liệu mẫu — sau này sẽ gọi API GET /api/bai-viet
const sampleBlogs = [
  {
    id: 1,
    title: '5 Bài Tập Giảm Mỡ Bụng Hiệu Quả Tại Nhà',
    content: 'Hướng dẫn chi tiết các bài tập đốt mỡ bụng hiệu quả nhất mà bạn có thể thực hiện tại nhà mà không cần dụng cụ...',
    thumbnail: 'https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?w=400&h=250&fit=crop',
    author: 'PT Minh Tuấn',
    createdAt: '2026-06-01',
  },
  {
    id: 2,
    title: 'Chế Độ Ăn Giảm Cân Cho Người Mới Bắt Đầu',
    content: 'Một chế độ ăn khoa học kết hợp tập luyện sẽ giúp bạn đạt được mục tiêu giảm cân nhanh chóng và bền vững...',
    thumbnail: 'https://images.unsplash.com/photo-1490645935967-10de6ba17061?w=400&h=250&fit=crop',
    author: 'Admin',
    createdAt: '2026-05-28',
  },
  {
    id: 3,
    title: 'Lợi Ích Của Việc Tập Gym Đều Đặn',
    content: 'Tập gym không chỉ giúp bạn có vóc dáng đẹp mà còn mang lại rất nhiều lợi ích cho sức khỏe tinh thần và thể chất...',
    thumbnail: 'https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=400&h=250&fit=crop',
    author: 'PT Hoàng Nam',
    createdAt: '2026-05-25',
  },
  {
    id: 4,
    title: 'Hướng Dẫn Tập Squat Đúng Kỹ Thuật',
    content: 'Squat là bài tập nền tảng nhất trong gym. Bài viết này sẽ hướng dẫn bạn cách thực hiện squat đúng kỹ thuật để tránh chấn thương...',
    thumbnail: 'https://images.unsplash.com/photo-1574680096145-d05b474e2155?w=400&h=250&fit=crop',
    author: 'PT Minh Tuấn',
    createdAt: '2026-05-20',
  },
  {
    id: 5,
    title: 'Protein Whey: Có Nên Dùng Không?',
    content: 'Protein whey là một trong những loại thực phẩm bổ sung phổ biến nhất trong giới gym. Nhưng liệu bạn có thực sự cần dùng nó?...',
    thumbnail: 'https://images.unsplash.com/photo-1593095948071-474c5cc2989d?w=400&h=250&fit=crop',
    author: 'Admin',
    createdAt: '2026-05-15',
  },
  {
    id: 6,
    title: 'Stretching: Tầm Quan Trọng Của Khởi Động',
    content: 'Nhiều người bỏ qua bước khởi động trước khi tập, đây là sai lầm nghiêm trọng có thể dẫn đến chấn thương...',
    thumbnail: 'https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?w=400&h=250&fit=crop',
    author: 'PT Hoàng Nam',
    createdAt: '2026-05-10',
  },
];

const BlogListPage = () => {
  return (
    <MainLayout>
      <div className="blog-page">
        <div className="blog-hero">
          <h1>Blog & Kiến Thức</h1>
          <p>Chia sẻ kiến thức tập luyện, dinh dưỡng và sức khỏe từ đội ngũ chuyên gia GymPro.</p>
        </div>

        <div className="blog-grid">
          {sampleBlogs.map((blog) => (
            <Link to={`/blog/${blog.id}`} key={blog.id} className="blog-card">
              <div className="blog-thumb">
                <img src={blog.thumbnail} alt={blog.title} />
              </div>
              <div className="blog-info">
                <h3 className="blog-title">{blog.title}</h3>
                <p className="blog-excerpt">{blog.content}</p>
                <div className="blog-meta">
                  <span><User size={14} /> {blog.author}</span>
                  <span><Clock size={14} /> {blog.createdAt}</span>
                </div>
              </div>
            </Link>
          ))}
        </div>
      </div>
    </MainLayout>
  );
};

export default BlogListPage;
