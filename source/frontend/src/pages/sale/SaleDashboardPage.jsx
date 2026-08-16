import { useCallback, useEffect, useState } from 'react';
import MainLayout from '../../components/layout/MainLayout';
import saleService from '../../services/saleService';
import './SaleDashboardPage.css';

const money = v => new Intl.NumberFormat('vi-VN').format(v || 0) + ' ₫';
const SaleDashboardPage = () => {
  const [dashboard, setDashboard] = useState(null); const [codes, setCodes] = useState([]);
  const [commissions, setCommissions] = useState([]); const [chats, setChats] = useState([]);
  const [selected, setSelected] = useState(null); const [messages, setMessages] = useState([]);
  const [message, setMessage] = useState(''); const [code, setCode] = useState(''); const [error, setError] = useState('');
  const load = useCallback(async () => {
    const [d,c,m,h] = await Promise.all([saleService.dashboard(),saleService.codes(),saleService.commissions(),saleService.chats()]);
    setDashboard(d);setCodes(c);setCommissions(m);setChats(h);
  }, []);
  useEffect(()=>{
    load().catch(e=>setError(e.response?.data?.message||'Không thể tải dữ liệu'));
    const timer = window.setInterval(() => load().catch(() => {}), 5000);
    return () => window.clearInterval(timer);
  },[load]);
  useEffect(() => {
    if (!selected?.id) return undefined;
    const refresh = () => saleService.messages(selected.id).then(setMessages).catch(() => {});
    const timer = window.setInterval(refresh, 3000);
    return () => window.clearInterval(timer);
  }, [selected?.id]);
  const openChat=async c=>{setSelected(c);setMessages(await saleService.messages(c.id));};
  const send=async e=>{e.preventDefault();if(!message.trim())return;await saleService.sendMessage(selected.id,message);setMessage('');setMessages(await saleService.messages(selected.id));};
  if(!dashboard)return <MainLayout><div className="sale-page">Đang tải...</div></MainLayout>;
  return <MainLayout><div className="sale-page"><h1>Trung Tâm Nhân Viên Sale</h1>{error&&<p className="sale-error">{error}</p>}
    <div className="sale-cards"><article><b>Cấp độ</b><strong>{dashboard.level}</strong><span>{dashboard.successfulCustomers} khách thành công</span></article>
      <article><b>Ưu đãi / Hoa hồng</b><strong>{dashboard.discountPercent}% / {dashboard.commissionRate}%</strong></article>
      <article><b>Hoa hồng chờ</b><strong>{money(dashboard.pendingCommission)}</strong></article>
      <article><b>Tư vấn đang mở</b><strong>{dashboard.activeChats}/3</strong></article></div>
    <button onClick={()=>saleService.setOnline(!dashboard.online).then(load)}>{dashboard.online?'Chuyển sang ngoại tuyến':'Bật trực tuyến nhận tư vấn'}</button>
    <div className="sale-grid"><section><h2>Mã giới thiệu ({codes.filter(c=>c.active).length}/3)</h2>
      <form onSubmit={async e=>{e.preventDefault();await saleService.createCode({code,oneTimePerMember:true});setCode('');load();}}><input required value={code} onChange={e=>setCode(e.target.value.toUpperCase())} placeholder="Mã mới"/><button>Tạo mã</button></form>
      {codes.map(c=><div className="sale-row" key={c.id}><b>{c.code}</b><span>-{c.discountPercent}% · {c.oneTimePerMember?'mỗi member 1 lần':'dùng nhiều lần'}</span><button onClick={()=>saleService.setCodeActive(c.id,!c.active).then(load)}>{c.active?'Lưu trữ':'Kích hoạt'}</button></div>)}</section>
      <section><h2>Tư vấn trực tiếp</h2><button onClick={()=>saleService.claimNext().then(load).catch(e=>setError(e.response?.data?.message))}>Nhận khách chờ lâu nhất</button>
        {chats.map(c=><button className="chat-pick" key={c.id} onClick={()=>openChat(c)}>{c.title} · {c.handoffStatus}</button>)}</section></div>
    {selected&&<section className="sale-chat"><div className="sale-chat-head"><h2>{selected.title}</h2><button onClick={()=>saleService.closeChat(selected.id).then(()=>{setSelected(null);load();})}>Kết thúc tư vấn</button></div>
      <div className="sale-messages">{messages.map(m=><p key={m.id} className={m.role==='SALE'?'mine':''}><b>{m.senderName||m.role}:</b> {m.content}</p>)}</div>
      <form onSubmit={send}><input value={message} onChange={e=>setMessage(e.target.value)} placeholder="Nhập nội dung tư vấn..."/><button>Gửi</button></form></section>}
    <section><h2>Hoa hồng</h2>{commissions.map(c=><div className="sale-row" key={c.id}><span>GD #{c.transactionId} · {c.memberName}</span><b>{money(c.commissionAmount)} · {c.status}</b></div>)}</section>
  </div></MainLayout>;
};
export default SaleDashboardPage;
