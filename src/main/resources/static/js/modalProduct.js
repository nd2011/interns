  document.addEventListener('DOMContentLoaded', function() {
      // Khởi tạo CKEditor khi modal show lần đầu
      let ckeditorInited = false;
      const modalEl = document.getElementById('addProductModal');
      if (modalEl) {
          modalEl.addEventListener('shown.bs.modal', function () {
              if (!ckeditorInited) {
                  CKEDITOR.replace('intro');
                  ckeditorInited = true;
              }
          });
      }

      // Xử lý submit form thêm sản phẩm
      const form = document.getElementById('addProductForm');
      const errorAlert = document.getElementById('formErrorAlertProduct');
      if (form) {
          form.addEventListener('submit', function(e) {
              e.preventDefault();
              // Chỉ update CKEditor nếu đã khởi tạo
              if (window.CKEDITOR && CKEDITOR.instances.intro) {
                  CKEDITOR.instances.intro.updateElement();
              }
              errorAlert.classList.add('d-none');
              const formData = new FormData(form);

              fetch('/product/products/add-ajax', {
                  method: 'POST',
                  body: formData
              })
              .then(response => {
                  // Xử lý nếu không phải JSON (ví dụ lỗi 405)
                  if (!response.ok) throw new Error("Server trả về lỗi " + response.status);
                  return response.json();
              })
              .then(result => {
                  if(result.success) {
                      addProductToTable(result.product);
                      bootstrap.Modal.getInstance(modalEl).hide();
                      form.reset();
                      if (window.CKEDITOR && CKEDITOR.instances.intro) {
                          CKEDITOR.instances.intro.setData('');
                      }
                  } else {
                      errorAlert.innerText = result.message || 'Đã có lỗi, vui lòng kiểm tra lại!';
                      errorAlert.classList.remove('d-none');
                  }
              })
              .catch(err => {
                  errorAlert.innerText = 'Không thể gửi dữ liệu: ' + err.message;
                  errorAlert.classList.remove('d-none');
              });
          });
      }
  });

  // Hàm thêm sản phẩm mới vào bảng
  function addProductToTable(product) {
      const tbody = document.querySelector('.product-table tbody');
      const vnd = value => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
      const tr = document.createElement('tr');
      tr.innerHTML = `
          <td>${product.id || ""}</td>
          <td>${product.name}</td>
          <td>${product.description}</td>
          <td>${vnd(product.costPrice)}</td>
          <td>${product.manufactureDate}</td>
          <td>${product.expiryDate}</td>
          <td>${product.quantity}</td>
          <td>${vnd(product.price)}</td>
          <td><img src="${product.image}" alt="product image" width="50" /></td>
          <td>
              <a href="/product/products/edit/${product.id}" class="btn btn-edit">Sửa</a>
          </td>
      `;
      tbody.prepend(tr);
  }