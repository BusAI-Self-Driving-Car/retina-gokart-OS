// THIS IS AN AUTOMATICALLY GENERATED FILE.  DO NOT MODIFY
// BY HAND!!
//
// Generated by lcm-gen

#include <string.h>
#include "idsc_BinaryBlob.h"

static int __idsc_BinaryBlob_hash_computed;
static uint64_t __idsc_BinaryBlob_hash;

uint64_t __idsc_BinaryBlob_hash_recursive(const __lcm_hash_ptr *p)
{
    const __lcm_hash_ptr *fp;
    for (fp = p; fp != NULL; fp = fp->parent)
        if (fp->v == __idsc_BinaryBlob_get_hash)
            return 0;

    __lcm_hash_ptr cp;
    cp.parent =  p;
    cp.v = (void*)__idsc_BinaryBlob_get_hash;
    (void) cp;

    uint64_t hash = (uint64_t)0x9c7079c442ed5c7cLL
         + __int32_t_hash_recursive(&cp)
         + __int8_t_hash_recursive(&cp)
        ;

    return (hash<<1) + ((hash>>63)&1);
}

int64_t __idsc_BinaryBlob_get_hash(void)
{
    if (!__idsc_BinaryBlob_hash_computed) {
        __idsc_BinaryBlob_hash = (int64_t)__idsc_BinaryBlob_hash_recursive(NULL);
        __idsc_BinaryBlob_hash_computed = 1;
    }

    return __idsc_BinaryBlob_hash;
}

int __idsc_BinaryBlob_encode_array(void *buf, int offset, int maxlen, const idsc_BinaryBlob *p, int elements)
{
    int pos = 0, element;
    int thislen;

    for (element = 0; element < elements; element++) {

        thislen = __int32_t_encode_array(buf, offset + pos, maxlen - pos, &(p[element].data_length), 1);
        if (thislen < 0) return thislen; else pos += thislen;

        thislen = __int8_t_encode_array(buf, offset + pos, maxlen - pos, p[element].data, p[element].data_length);
        if (thislen < 0) return thislen; else pos += thislen;

    }
    return pos;
}

int idsc_BinaryBlob_encode(void *buf, int offset, int maxlen, const idsc_BinaryBlob *p)
{
    int pos = 0, thislen;
    int64_t hash = __idsc_BinaryBlob_get_hash();

    thislen = __int64_t_encode_array(buf, offset + pos, maxlen - pos, &hash, 1);
    if (thislen < 0) return thislen; else pos += thislen;

    thislen = __idsc_BinaryBlob_encode_array(buf, offset + pos, maxlen - pos, p, 1);
    if (thislen < 0) return thislen; else pos += thislen;

    return pos;
}

int __idsc_BinaryBlob_encoded_array_size(const idsc_BinaryBlob *p, int elements)
{
    int size = 0, element;
    for (element = 0; element < elements; element++) {

        size += __int32_t_encoded_array_size(&(p[element].data_length), 1);

        size += __int8_t_encoded_array_size(p[element].data, p[element].data_length);

    }
    return size;
}

int idsc_BinaryBlob_encoded_size(const idsc_BinaryBlob *p)
{
    return 8 + __idsc_BinaryBlob_encoded_array_size(p, 1);
}

int __idsc_BinaryBlob_decode_array(const void *buf, int offset, int maxlen, idsc_BinaryBlob *p, int elements)
{
    int pos = 0, thislen, element;

    for (element = 0; element < elements; element++) {

        thislen = __int32_t_decode_array(buf, offset + pos, maxlen - pos, &(p[element].data_length), 1);
        if (thislen < 0) return thislen; else pos += thislen;

        p[element].data = (int8_t*) lcm_malloc(sizeof(int8_t) * p[element].data_length);
        thislen = __int8_t_decode_array(buf, offset + pos, maxlen - pos, p[element].data, p[element].data_length);
        if (thislen < 0) return thislen; else pos += thislen;

    }
    return pos;
}

int __idsc_BinaryBlob_decode_array_cleanup(idsc_BinaryBlob *p, int elements)
{
    int element;
    for (element = 0; element < elements; element++) {

        __int32_t_decode_array_cleanup(&(p[element].data_length), 1);

        __int8_t_decode_array_cleanup(p[element].data, p[element].data_length);
        if (p[element].data) free(p[element].data);

    }
    return 0;
}

int idsc_BinaryBlob_decode(const void *buf, int offset, int maxlen, idsc_BinaryBlob *p)
{
    int pos = 0, thislen;
    int64_t hash = __idsc_BinaryBlob_get_hash();

    int64_t this_hash;
    thislen = __int64_t_decode_array(buf, offset + pos, maxlen - pos, &this_hash, 1);
    if (thislen < 0) return thislen; else pos += thislen;
    if (this_hash != hash) return -1;

    thislen = __idsc_BinaryBlob_decode_array(buf, offset + pos, maxlen - pos, p, 1);
    if (thislen < 0) return thislen; else pos += thislen;

    return pos;
}

int idsc_BinaryBlob_decode_cleanup(idsc_BinaryBlob *p)
{
    return __idsc_BinaryBlob_decode_array_cleanup(p, 1);
}

int __idsc_BinaryBlob_clone_array(const idsc_BinaryBlob *p, idsc_BinaryBlob *q, int elements)
{
    int element;
    for (element = 0; element < elements; element++) {

        __int32_t_clone_array(&(p[element].data_length), &(q[element].data_length), 1);

        q[element].data = (int8_t*) lcm_malloc(sizeof(int8_t) * q[element].data_length);
        __int8_t_clone_array(p[element].data, q[element].data, p[element].data_length);

    }
    return 0;
}

idsc_BinaryBlob *idsc_BinaryBlob_copy(const idsc_BinaryBlob *p)
{
    idsc_BinaryBlob *q = (idsc_BinaryBlob*) malloc(sizeof(idsc_BinaryBlob));
    __idsc_BinaryBlob_clone_array(p, q, 1);
    return q;
}

void idsc_BinaryBlob_destroy(idsc_BinaryBlob *p)
{
    __idsc_BinaryBlob_decode_array_cleanup(p, 1);
    free(p);
}

int idsc_BinaryBlob_publish(lcm_t *lc, const char *channel, const idsc_BinaryBlob *p)
{
      int max_data_size = idsc_BinaryBlob_encoded_size (p);
      uint8_t *buf = (uint8_t*) malloc (max_data_size);
      if (!buf) return -1;
      int data_size = idsc_BinaryBlob_encode (buf, 0, max_data_size, p);
      if (data_size < 0) {
          free (buf);
          return data_size;
      }
      int status = lcm_publish (lc, channel, buf, data_size);
      free (buf);
      return status;
}

struct _idsc_BinaryBlob_subscription_t {
    idsc_BinaryBlob_handler_t user_handler;
    void *userdata;
    lcm_subscription_t *lc_h;
};
static
void idsc_BinaryBlob_handler_stub (const lcm_recv_buf_t *rbuf,
                            const char *channel, void *userdata)
{
    int status;
    idsc_BinaryBlob p;
    memset(&p, 0, sizeof(idsc_BinaryBlob));
    status = idsc_BinaryBlob_decode (rbuf->data, 0, rbuf->data_size, &p);
    if (status < 0) {
        fprintf (stderr, "error %d decoding idsc_BinaryBlob!!!\n", status);
        return;
    }

    idsc_BinaryBlob_subscription_t *h = (idsc_BinaryBlob_subscription_t*) userdata;
    h->user_handler (rbuf, channel, &p, h->userdata);

    idsc_BinaryBlob_decode_cleanup (&p);
}

idsc_BinaryBlob_subscription_t* idsc_BinaryBlob_subscribe (lcm_t *lcm,
                    const char *channel,
                    idsc_BinaryBlob_handler_t f, void *userdata)
{
    idsc_BinaryBlob_subscription_t *n = (idsc_BinaryBlob_subscription_t*)
                       malloc(sizeof(idsc_BinaryBlob_subscription_t));
    n->user_handler = f;
    n->userdata = userdata;
    n->lc_h = lcm_subscribe (lcm, channel,
                                 idsc_BinaryBlob_handler_stub, n);
    if (n->lc_h == NULL) {
        fprintf (stderr,"couldn't reg idsc_BinaryBlob LCM handler!\n");
        free (n);
        return NULL;
    }
    return n;
}

int idsc_BinaryBlob_subscription_set_queue_capacity (idsc_BinaryBlob_subscription_t* subs,
                              int num_messages)
{
    return lcm_subscription_set_queue_capacity (subs->lc_h, num_messages);
}

int idsc_BinaryBlob_unsubscribe(lcm_t *lcm, idsc_BinaryBlob_subscription_t* hid)
{
    int status = lcm_unsubscribe (lcm, hid->lc_h);
    if (0 != status) {
        fprintf(stderr,
           "couldn't unsubscribe idsc_BinaryBlob_handler %p!\n", hid);
        return -1;
    }
    free (hid);
    return 0;
}

